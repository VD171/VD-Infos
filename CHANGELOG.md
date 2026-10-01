# Changelog

All notable changes to VD Infos. Dates are ISO (YYYY-MM-DD).

The 2.x line is a ground-up rewrite; the last public 1.x release was
[v1.11-beta6](https://github.com/VD171/VD-Infos/releases/tag/v1.11-beta6)
(2024-12-02). Everything between it and 2.00 is the rewrite described below.

## [Unreleased]

## [2.22] - 2026-10-01

- **New probe: FLAG_SECURE self-verify.** `integrity:flagsecure_honored` sets `FLAG_SECURE` on the app's own window, reads it back, then clears it. A screenshot-unlock hook on `Window.addFlags`/`setFlags` drops the bit, so a flag read back absent (`STRIPPED`) is the tell. The SurfaceFlinger compositor-side variant leaves the window flag set and is out of scope here.

- **EC-vs-RSA attestation split into three probes.** The old single `attest:keybox_ec_vs_rsa` card is replaced by three, each with its own verdict:
  - `attest:rot_ec_vs_rsa` - compares the root of trust (verified boot state, device-locked, boot key, root) read through an EC request vs an RSA request; a divergence means the RSA path is not covered by the keybox. Security level is not voted (the EC key is StrongBox-requested, the RSA one is not).
  - `attest:leaf_sig_alg` - an RSA attestation leaf whose certificate is signed with ECDSA instead of RSA is the tell of an EC-only keybox that forged the RSA path with its EC key.
  - `attest:batch_key_ec_vs_rsa` - the EC and RSA chains should be signed by different attestation keys; a shared signer (certificate serial or public key) is the EC-only tell, and a divergent subject serialNumber (device identity) is a separate tell. The common name is not compared. Serial and identity show as SHA-256 fingerprints, raw behind an eye and out of the exported JSON, like `attest:sb_tee_signer`.

- **New probe: network-type spoof consistency.** `net:nettype_consistency` compares the transport the framework reports (`hasTransport`) against the active interface name (`LinkProperties.getInterfaceName`) and the wlan state from sysfs. "mobile" over a `wlan` interface is the spoof.

- **New probe: StrongBox attestation level.** `attest:strongbox_level` requests a StrongBox-backed key (where the feature is present) and checks `attestationSecurityLevel`. A TEE-level result, or no StrongBox attestation, is the tell of a TEE keybox posing as StrongBox. Complements `attest:sb_tee_signer`; not applicable without the StrongBox feature.

- **`prop:variants:*` no longer flags legitimate rebrands and GSIs as a split.** Sub-brands shipping another brand's system image (POCO/Redmi on Xiaomi, OnePlus/oplus/OPPO/realme) and GSIs (`generic_system_google`) read as cross-partition divergences. Generic partitions are now dropped from the vote and values canonicalized through `assets/data/prop_identity_groups.txt` before comparison; an unexplained split still turns red. The matrix still shows the raw per-partition values.

- **A broken shell `pm` no longer reads as a package-count divergence.** When the non-root `pm` fails (`Failure calling service package`), the count probes matched the error text in a loose `*package:*` and reported `0`, diverging from the real `getInstalledPackages()`. They now count only `^package:` lines and treat a failure as unavailable (dropped from the vote) rather than a `0`. Fixes a common false alarm on heavily-modified / budget ROMs.

- **`hw:cpu_cores` compares total against total.** It was voting online-core counts (`availableProcessors`, `_SC_NPROCESSORS_ONLN`, which drop when the kernel parks cores) against total (`present`), so a big.LITTLE device with parked cores diverged. The vote is now total-only (`_SC_NPROCESSORS_CONF`, `present`); online counts stay as reference rows.

- **The per-probe note moved into the info dialog.** The lightbulb opens on any probe with a note or a fix (was fix-only), showing the note first and the fix below; nothing floats under the card.

- **The StrongBox/TEE signer card carries no note.** The SHARED-vs-distinct verdict and the two fingerprints speak for themselves; the fingerprint keeps its eye to reveal the serial.

- **`attest:sb_tee_signer` is gated on the StrongBox feature.** A device without StrongBox no longer compares a TEE signer against an absent one (a false divergence) and reports not-applicable; where the feature is present but no StrongBox attestation is produced, `STRONGBOX_MISSING` is surfaced.

- **New probe: restricted setting read gate.** `integrity:settings_read_gate` reads a key that Android 12+ redacts from apps targeting SDK 31+. It runs only when both the device and the app target are 31+ (so the SDK-27 compat flavor is not-applicable, not divergent); there, a non-null value is the tell of a hiding layer answering before the platform's read check.

## [2.21] - 2026-09-28

- **The StrongBox/TEE signer detail shows a fingerprint by default.** `attest:sb_tee_signer` reports SHARED vs distinct as before; the reference line now shows a 16-hex SHA-256 fingerprint of each serial (equal serials fingerprint equal), with an eye for the original, and the raw serial is kept out of the exported JSON.

- **Kernel identity cross-checked across the syscall, sysctls and property.** `kernel:version_claim` compares `ro.kernel.version` against the real `uname(2)` release (a property spoofed over a different kernel diverges). `kernel:release` adds `/proc/sys/kernel/osrelease` and `/proc/version`; the new `kernel:version_string` cross-checks the `#N SMP ...` line across `uname(2)`, `uname -v`, `/proc/sys/kernel/version` and `/proc/version`. `kernel:selfbuild` surfaces build provenance from `/proc/version` (builder, compiler, build id). New `kernel:build_date` (kernel built well after the ROM), `kernel:config` (KSU/SUSFS/local version in `/proc/config.gz`) and `kernel:modules` (ksu/susfs/magisk in `/sys/module` and `/proc/kallsyms`). File-backed lenses idle cleanly where the sandbox denies them.

- **Five new all-apps checks.** `pkg:xposed_scan` flags installed packages carrying Xposed manifest meta-data (`xposedmodule`, etc.). `pkg:debuggable_scan` lists apps with `FLAG_DEBUGGABLE`/`FLAG_TEST_ONLY`. `pkg:uid_groups` flags a user app sharing a Linux UID with a system app. `pkg:updated_system` lists `FLAG_UPDATED_SYSTEM_APP` packages (a sideload replaced a system app). `isrc:landscape` groups visible apps by recorded installer.

- **Key attestation is read three ways and cross-checked.** Device-properties, plain TEE and (where available) StrongBox attestations are lined up on the RootOfTrust and patch fields (verified boot hash/key/state, device-locked, patches); a genuine device returns the same RootOfTrust for every mode, so one that differs by mode diverges. Each field votes across device-properties, plain, a second challenge and an RSA attestation (StrongBox for reference). `attest:challenge_echo` checks the attestation echoes the fresh random challenge (a replayed keybox fails). The attested serial joins the device-ID cross-check.

- **Cleaner lens tags for the attestation and native variants.** `JNI 92B`, `TEE Plain`, `TEE StrongBox`, `TEE RSA`, `TEE Alt` show as their own chip instead of a method-name suffix.

- **Live GPU identity is back, read two ways.** `gpu:vendor/renderer/version/glsl` read `glGetString` through an offscreen EGL context via a JVM lens and a native EGL lens (plus `ActivityManager` GLES for reference), so a Java-side `glGetString` hook diverges. The emulator-telling renderer string (SwiftShader etc.) is visible again.

- **More telephony identity fields.** Type Allocation Code (cross-checked against the first eight IMEI digits), manufacturer code, SIM carrier id / name / specific carrier id, and the visual-voicemail package now join the telephony surface.

- **Two coverage probes carried over from 1.x.** `integrity:keychain_blacklist` reads mtime/size of `/data/misc/keychain/pubkey_blacklist.txt` three ways (a cert install rewrites it). `integrity:buildprop_vs_runtime` flags a key whose runtime value was `resetprop`'d away from the on-disk `build.prop`.

- **New property keys in the catalog.** `ril.rfcal_date`, `ril.manufacturedate`, `ro.product.nickname` (under Device), `persist.sys.exif.model` (under Model).

- **The boot-count cross-check is directional now.** `Phenotype_boot_count` (Play services) legitimately trails `boot_count` when GMS skips a boot-commit, so only the impossible direction - the snapshot ahead of `boot_count` (a reset) - fires. Zeroing `boot_count` to fake a fresh device is still caught; trailing no longer false-flags.

- **Repeated per-partition properties are cross-checked by lineage.** `prop:variants:*` reads every partition variant a device exposes (partitions in `assets/data/partitions.txt`, fields and scope in `prop_families.txt`): identity fields (brand, manufacturer, device, tags, type) must match across all partitions, lineage fields (fingerprint, model, build id, dates, versions) within each lineage (system/vendor/product/bootimage) - so a GRF device with different system/vendor Android versions reads clean while a partial fingerprint change splits a lineage. Shows the full matrix. `prop:enum_vs_getprop` diffs the native property enumeration (`__system_property_foreach`) against `getprop` from another process. Partition discovery is dynamic.

- **`integrity:buildprop_vs_runtime` reads the real per-partition paths.** It was checking `/system_ext/build.prop` and `/product/build.prop`, which many devices keep under `.../etc/`; paths now come from `partitions.txt` and cover every partition, `system_dlkm`/`vendor_dlkm` included.

- **Coverage swept against the 1.x Java build.** Fourteen dropped property keys are back in the catalog (`gsm.version.ril-impl`, `ro.baseband.arch`, `ro.hardware.chipname`, `persist.graphics.egl`, the OEM manufacture-date props, `sys.kernel.firstboot`, ...), and its disguised-hider packages (`com.google.android.hmal`, `com.tsng.dyhhvf`, Android Faker) joined the detection lists.

- **Three app-perspective verdicts.** `integrity:ime_verdict` flags a non-system keyboard installed from outside the store allowlist. `integrity:store_authenticity` checks Play Store/GMS/GSF carry Google's signing certificate (`known_certs.txt`); another key is a fake/microG/repack. `integrity:devgate` flags a developer-gated setting (`dev_gated_settings.txt`) left non-default while `development_settings_enabled=0`; each shows its AOSP default (`adb_allowed_connection_time` defaults to `604800000`).

- **`/data/local/tmp` is listed, not just probed by name.** `integrity:local_tmp_list` does a `getdents64` and surfaces any entry an app can reach, module drops included. A locked device keeps it unlistable (EACCES, the clean answer); a visible entry means residue is app-reachable.

- **Divergences are confirmed by a second read.** On `MISMATCH` the engine re-measures once and reports the settled read only if the divergence does not reproduce - it never downgrades one that survives, so a real detection is never hidden, only flicker (a racy provider/binder lens) is removed. Cached probes (attestation, GPU) are unaffected.

- **Fixed: the `content://settings` `call(GET_<store>)` phantom that faked identifier divergences.** On some devices (ColorOS/OnePlus) `call("GET_global", key)` intermittently returned a phantom `"0"` for a key in another namespace, flickering `id:android_id`, `id:ads`, the `spoof:*` sweep and some `set:*` red then green. The call reader now drops a lone `"0"` when a real value exists in another store, keeping a genuine `"0"` (adb_enabled etc.) intact.

- **New: StrongBox vs TEE signer cross-check.** `attest:sb_tee_signer` generates a key at each level and flags a shared signing certificate, which is a single keybox (TrickyStore) faking both. Offline; the keybox-revocation network check is left out to keep VD Infos network-free.

- **New: executable-maps injection classifier and hidden-superblock scan.** `integrity:maps_exec` flags an executable `(deleted)`/`memfd`/`ashmem` mapping only when it is RWX or begins with the ELF magic, so a real unlinked `.so` is caught while the ART JIT trampoline is benign. `integrity:anon_minor` finds a superblock hidden in a private namespace (anon-bdev minor present via `stat()` but absent from mountinfo), ignoring long-run churn.

- **New: loader-module scan that survives maps-scrubbing.** `integrity:dlphdr` enumerates modules through `dl_iterate_phdr` (the linker), not `/proc/self/maps`, so a hook that scrubs its maps entry (Shamiko etc.) is still listed; names are read fault-safe, page by page, so a scrubbed string cannot SIGSEGV the scan. `integrity:frida_port` flags an open `127.0.0.1:27042/27043` (the classic Frida server/gadget).

- **New: catches an app-hider that only prunes the package list.** `integrity:concealment` takes a community-editable target list (`assets/data/conceal_targets.txt`) and, for each target absent from `getInstalledPackages`, tries the doors a partial hider forgets: `getPackageInfo`, `getApplicationInfo`, `createPackageContext` (+ its APK as a `ZipFile`), the launch intent, and a `MAIN` intent. A target hidden from the listing but still reachable through any of them is the leak; it prints the per-target `L/P/A/C/Z/I/R` door matrix and turns red only then. A hider that closes every door reads clean.

- **Signing certificates now say whose they are.** A community-editable catalog `assets/data/known_certs.txt` (`SHA-256<tab>owner`) labels a known certificate wherever the app prints one (installer cross-check, initiator-signature gap, signing digests, this app's own signature). Ships with Google, Motorola, Xiaomi (`CN=MIUI`), Telegram, Indus Appstore and VD171 keys.

- **The installer-certificate check names the installer.** Both lenses of `isrc:self_initiator_sig` print the initiating package next to the hash (`com.miui.packageinstaller: c9009d01… [Xiaomi, CN=MIUI]`), and a note lists the causes; the verdict is unchanged. Seen on Xiaomi.eu HyperOS 3: `getPackageInfo()` reports Xiaomi's cert for the installer while the install record holds another (`f87bd41b…`) - that ROM re-signs the system, so the two APIs genuinely disagree and the divergence stays.

## [2.20] - 2026-09-23

- **VD Infos is now open source.** The full app (Kotlin/Compose, the native lens, the Gradle build) is
  published under the GNU AGPL v3 with the docs and screenshots. Run a modified copy, even as a service,
  and you must offer its source.

- **The detection data is community-editable.** The package lists, su paths, SELinux contexts, injection
  needles, the Build-to-property map, the invisible-character ranges and the rest now live as plain-text
  `assets/data/*.txt` read at runtime. Each shell probe builds its `grep` pattern from the same list, so
  a native lens and its shell twin cannot drift apart.

- **Invisible-name detection widened, and now cross-checked by lens.** `integrity:zero_width_names`
  decoded only three 3-byte zero-width ranges; it now decodes real UTF-8 (2/3/4-byte) against an
  editable range list that also covers the soft hyphen, the Hangul fillers, the braille blank, the
  variation selectors and the 4-byte TAGS block. A JVM `File.list` lens was added next to the native
  `getdents64` scan, both reading the same ranges, so a hook that filters an invisible entry from the
  Java listing but not from the raw syscall now shows up as a divergence.

- **Changing the app language no longer needs a restart to retitle the probes.** Probe titles are
  resolved when the scan engine is built, and the language switch kept the old engine, so the titles
  stayed in the previous language until the app was reopened. The switch now rebuilds the engine, so
  every title follows the chosen language at once.

- **The SIM/network operator fields no longer diverge on a dual-SIM phone.** `TelephonyManager`
  follows the active-data subscription while the `gsm.*` operator props without a slot suffix follow
  whatever slot the RIL last wrote, so on a dual-SIM phone with two carriers the JVM and the prop
  read different SIMs (e.g. 51001 vs 51010) - a slot ambiguity, not a hook, unresolvable from an app.
  The six operator/name/ISO fields (SIM and network) now show every value but leave the prop out of
  the verdict, so the value is still visible and the false divergence is gone.

- **The initiator-certificate cross-check now covers every app, not only the known stores.** The
  all-apps `initiator cert recorded != installed` gap was gated to initiators in the built-in store
  list, so an app installed by a custom or unlisted installer that was later re-signed slipped past
  it - the self probe had no such gate. It now checks any installed initiator that is not a bare
  sideloader; a v3 key rotation still shares the lineage and does not trip.

- **New: the loaded SELinux policy answers for itself.** `selinux:status` reads the kernel status
  page (the `policyload` counter a runtime policy injection bumps, plus `seq`), and `selinux:policy_probe`
  asks the policy which types it knows and what it grants this app, through selinuxfs and the kernel's
  own AVC. A stock device shuts that door, so a refusal is the normal answer and a policy that lets an
  app interrogate it is the finding.
- **New: root/Xposed managers checked through every PackageManager door.** `integrity:manager_doors`
  asks the same "is this manager here?" through the package list, `getPackageInfo`, `getApplicationInfo`,
  the launch intent, the component tables and `getPackagesHoldingPermissions`, and flags a manager that
  answers on one door but hides on another - the seam a detector walks through by broadcasting straight
  at a `Probe_<random>` receiver. Seven detector-target packages were added to the lists.

- **Three settings-spoof keys added to the matrix, to mirror the house apply/verify set.**
  `verifier_verify_adb_installs` (INT), `install_non_market_apps` (INT) and `default_input_method`
  (STR) are applied on the real system and checked by the sibling verifier, but VD Infos did not
  probe them. Added to `spoof_keys.txt`, so each is now swept across every read route (getString,
  getInt for the two INTs, appended-path, `name=?` selection, bulk-all, provider `call GET_<store>`,
  and the shell reference) in all three stores. Probe count 907 -> 910.

- **Two false divergences on a clean device are gone.** `selinux:context` compared the kernel file
  (`u:r:...`) against `id -Z`, but some toybox builds print `context=u:r:...`; the shell read now
  drops that prefix, so the identical context stops reading as a mismatch. And `hw:cpuinfo` dropped the
  legacy `Processor: AArch64 Processor rev N` header line, which reflects whichever core the reader was
  scheduled on and so swung between clusters on a big.LITTLE SoC (rev 4 vs rev 2); the per-core blocks,
  the real CPU identity a hook would fake, are still compared. Reported from a locked-bootloader device.

- **New: nine detector-frontier probes.** The surfaces a hostile app interrogates for root, hiding
  and injection, rebuilt in VD Infos' idiom in a new `FrontierProbes`. For the structural
  `/proc` reads the verdict is driven by LENS AGREEMENT on a normalised digest, never a raw threshold,
  so a filter that hides an entry from one lens diverges while an unusually-laid-out ROM does not
  false-positive. (1) `net:sock_diag` - a confined app must be denied a `NETLINK_SOCK_DIAG` socket; a
  reply means the netlink policy was loosened (new native entry point, and by design it answers on the
  compat/`untrusted_app_27` build and refuses on modern). (2) `mount:peer_gap` - the mount peer-group
  id set read three ways; a per-lens gap is a hidden mount. (3) `mount:fdinfo_mnt` - an open descriptor
  whose `mnt_id` is absent from mountinfo points at a mount hidden from the view. (4) `proc:readproc_gid`
  - a missing AID_READPROC (GID 3009) is a trace some app-hiders leave. (5) `proc:fd_graph` - a
  descriptor census (informational). (6) `mem:anon_exec` - executable pages with no backing file, the
  typical injection/Zygisk residue, counted and cross-checked. (7) `kernel:selfbuild` - `-dirty`, git
  and SUSFS build markers a stock kernel never carries. (8) `tee:soter` - the Tencent Soter service
  program against its service property. (9) `proc:cgroup_format` - a cloned/dual-app container diverging
  from the canonical `uid_/pid_` unified cgroup path. All strings localised across the 21 locales.
  Probe count 913 -> 922; the READMEs' count is updated in every locale.

- **Detector-blacklist parity: 52 packages added, curated by philosophy.** The missing
  stealth-relevant entries from a detector's package blacklist were routed into the matching lists -
  `root_tools_apps` (+17), `suspicious_apps` (+30), `hiding_apps` (+3), `xposed_manager_apps` (+2). The
  pure game-cheat / injection-cheat cluster (21 packages) was intentionally left out: it only adds noise
  to a privacy-phone scan and does not bear on the stealth audit.

## [2.19.1] - 2026-09-21

- **The installer-cert probe no longer cries wolf over a key rotation.** `self_initiator_sig` and the all-apps `initiator cert recorded != installed` gap now compare the whole signing lineage (same identity if the cert sets share any certificate), so an installer that rotated its key (v3 rotation, e.g. Google Play) no longer diverges; only a disjoint set - a swapped or repackaged installer - does.

## [2.19] - 2026-09-21

- **New: the install-source surface, read two ways, over every app.** Installer, initiating, originating and packageSource are each read through every lens (`getInstallSourceInfo`, legacy `getInstallerPackageName`, `dumpsys`, `pm`), so a hook rewriting one path diverges; a build that omits a field (MIUI) reports a refusal, not a fake null. One pass over all apps cross-checks installer / initiating / initiator-signature / originating / FLAG_SYSTEM and lists the apps per gap. The root-free tell: the JVM lens can be hooked to dress a sideload as the Play Store while `pm` in another process stays untouched, so JVM-names-a-store vs pm-denies exposes it. The initiator's recorded certificate is compared to that package's current one, unpinned, self-calibrating per device.

- **The installed-packages and installed-applications counts name the divergent packages.** When the framework set and `pm` disagree, two companion items list which packages each lens sees and the other does not (only-JVM, only-pm), scoped to the same user.

- **A package count that could not be read no longer reports zero.** The shell lens discarded the error stream, so a `pm` refusal became a legitimate-looking `0`. The error is read and reported as a refusal; the lens is also scoped to the app's own user (Xiaomi Dual Apps defaults `pm` to user 999, so the two lenses were describing different users).

- **Remote key provisioning is read by the chain's shape, not only the signer's lifetime.** Besides the signer lifetime (short = remote, years = batch), the probe now checks the shape Google's verifier uses (the cert under the root is `Droid CA2`) and compares the two; they agree on a genuine device, so a split is the finding. Root name and signer lifetime shown for context.
- **New probe: attestation ProvisioningInfo.** The remote-provisioning extension (`1.3.6.1.4.1.11129.2.1.30`) is decoded: certs issued in the last 30 days, manufacturer, attested entity, lost-device flag. Undocumented keys are shown as-is, not errored.
- **The boot counter is read from two independent writers and compared.** The framework's `boot_count` and Play Services' `Phenotype_boot_count` are read side by side; a `boot_count` reset to look freshly flashed while the Play Services copy keeps the real count diverges here. They agree on an untouched device (Android 11, 12, 16).

## [2.18] - 2026-09-18

- **The security-patch probes point at the fix that applies to each.** The three attested-patch probes (OS, vendor, boot) now name the keybox tools that change attestation (TrickyStore `security_patch.txt`, OhMyKeymint `config.toml [trust]`); the two framework-vs-property probes say those will not help there, since the gap is a property override to align with resetprop.
- **Two lenses that say the same thing in different words no longer look divergent.** When every comparable reading of a probe is a yes/no word (`true`/`1`/`yes`/`on`/`enabled` vs `false`/`0`/`no`/`off`/`disabled`), the verdict is decided on polarity, not exact text. Non-boolean values (a model name, a patch level, a SIM operator) are compared exactly as before.

- **A finished scan with nothing to report says so.** On a COMPLETED scan with no divergence the headline turns green with a trophy and a check. It reports agreement between lenses, not a clean device - a hook that rewrites every route coherently reads this way too.

- **The "how to fix" stopped offering a fix that does not fix.** Five probes (loaded kernel modules, an inline libc hook, `/data/local/tmp` drops, root backup tools by path, this app's debuggable flag) carried the hide-a-package note, which does not apply to them; the note stays only on the eight probes that list installed apps.
- **A lens that cannot see no longer answers "no".** On a modern target an app cannot enumerate network interfaces or list `/sys/class/net`, so the tun/ppp/tap readings answered `false` out of blindness and outvoted the connectivity API's correct active-VPN. They report the new `RESTRICTED` token (shown, never votes); where the platform allows the look (compat target, older releases) they answer as before.
- **The screen no longer mixes two languages.** The in-app language choice re-tagged only the activity while probe titles resolved from the application context (the system language). The application context is re-tagged too now, so one language answers for the whole app.
- **Two false divergences are gone.** `This app: debuggable / debug build` compared the app's `FLAG_DEBUGGABLE` against system-wide `ro.debuggable` (a release app on a userdebug ROM diverged from itself); the global property is context now, not compared. And `com.rarlab.rar` left the root-backup-tools list (a general archiver).
- **A probe that runs out of time says so in your language.** The timeout message was the last of the app's own prose still hardcoded in English.
- **More detection data became community-editable.** The kernel-module needles, the root-backup package list, the `/data/local/tmp` artifact names and the advertising-id key family moved into `assets/data`. What stays in code is structural only (the `su` lookup PATH, the partition set, the settings stores, the SDK's app-op constants).

## [2.17] - 2026-09-16

- **Coverage checkup (2026-09-16): +20 probes, 865 -> 885.** A cross-tool audit against the house evasion stack and a commercial native RASP, each item read through more than one route. New: `integrity:kmods` (loaded kernel modules via `/proc/modules`+`lsmod`, catches KSU-Next LKM mode); `integrity:kernel_syscall_age` (a syscall newer than `uname` claims is an old-kernel-in-new-ROM spoof); `integrity:su_libc_hook` (su paths by raw syscall vs libc vs Java - a split is an inline libc hook); `integrity:self_lib_origin` (dladdr on our `.so`: `(deleted)`/`memfd:`/`/data/adb` = injected); `integrity:uid_coherence` (process uid vs data-dir owner); `integrity:backup_tools`, `integrity:local_tmp`, `integrity:documents_providers`, `integrity:zero_width_names`, `net:resolv_conf`, `pkg:store_version`, `self:debuggable`; `attest:key_secure_hw`, `attest:keybox_ec_vs_rsa`, `attest:app_id` (attestationApplicationId vs our real package+signature). LineageOS system features joined the per-feature set.


- **Releases signed with v2 + v3.** The APK Signature Scheme v3 was enabled (v2 was already on), so both APKs carry the v2+v3 signatures under the same key; the v3 block also leaves room for a future key rotation. Two APKs per release as before: `SDK_35` (strict modern sandbox) and `SDK_27` (looser domain, for comparison).
- **The locale probe no longer diverges against the app's own language setting.** Its JVM lens read the process default locale, which the in-app language choice re-tags, so a chosen language (`pt`) diverged from the device property (`pt-BR`). It now reads the system resources, which no app setting re-tags.
- **Two vendor device UUIDs added** (issue #7, reported by w3struk): `extm_uuid` (Xiaomi) and `op_security_uuid` (OnePlus), both identity-bearing, read through every settings route and in the spoof-consistency matrix.
- **Community-editable data lists.** The detection package lists, the ~495-entry property catalog and the settings-spoof matrix moved out of Kotlin into plain-text `assets/data/` (one entry per line, `#` comments), loaded and cached at runtime via `AssetData`.
- **No more background work; the app closes itself when idle.** The periodic drift scanner (`SnapshotWorker`) and its notification are gone, along with `POST_NOTIFICATIONS` and the WorkManager dependency. After a few idle minutes in the background the app finishes itself and drops out of recents.
- **HMA-OSS credited and linked.** In-app solutions mentioning HMA-OSS link to its source (github.com/frknkrc44/HMA-OSS), the READMEs gained an Acknowledgements section, and `HMA-OSS.md` collects the project's links.
- **One-time language chooser + per-app language.** First launch offers the app language ("System default" included), saved in SharedPreferences and never asked again. Each language is listed by its own-script endonym; the choice re-tags the activity context in `attachBaseContext` (no appcompat, no framework locale service). All 21 UI languages are selectable.
- **Title bar tidied.** The version moved up next to the app name; the line below carries only the tagline and target SDK.
- **About dialog redesigned.** A header with emblem, app name and version pill; the info grouped into rounded cards with a leading icon per row (developer, source, license, target SDK, contacts, support) under accent section labels, and the privacy note as a highlighted panel.
- **A "how to fix" button on common divergences.** Probes with a solution show a lightbulb next to the verdict that opens the fix in a dialog. Solution text is per-probe, assigned deliberately. Three notes back the wired probes:
  - keybox `target.txt` note (TrickyStore/TEE Simulator/OhMyKeyMint) -> the boot/attestation probes (`Device locked`, verified boot state/key/hash, attestation security level, keymaster version, attested OS version and the OS/boot/vendor patch levels, attestation provisioning).
  - HMA-OSS / Hide My Applist hide-target note -> the installed-app lists (root detector/manager apps, root-based tools, apps requiring root, suspicious apps, analysis/emulator apps, hiding apps, Xposed managers).
  - HMA-OSS spoof-preset note -> `Spoof consistency: *`, `ADB enabled`, `Developer options enabled`.
- **18 new UI languages.** Full translations for Spanish, Italian, German, French, Russian, Indonesian, Turkish, Polish, Dutch, Swedish, Czech, Vietnamese, Chinese, Japanese, Korean, Persian, Hindi, Arabic and Thai, joining English and Portuguese. Source labels and value tokens stay untranslated by design; RTL is handled by `supportsRtl`.
- **No hardcoded UI copy left.** The one remaining literal placeholder (`(empty)`, shown for a lens
  that read null) moved to a `reading_empty` string resource, translated across all 21 locales.

## [2.16.2] - 2026-09-14

- **A shell blocked by the sandbox no longer fakes a divergence.** On a modern target the `untrusted_app` domain refuses a spawned shell `pm`/`settings`/`dumpsys` (EACCES) while the JVM/JNI API reads the fact; that shell EACCES was voting against the value another lens read. A shell EACCES/DENIED no longer votes when a non-shell lens read the value (it still votes when every lens refuses, and on the compat target). `absent`/`(none)` - the command ran and found nothing - still votes, so the advertising-id injection and spoofed-value tells are untouched.

## [2.16.1] - 2026-09-14

- **Per-key spoof-consistency compares the value, not which store holds it.** A "moved" setting (`adb_enabled` etc. live in Global) makes read paths report the key in different sets of stores (`global=0,system=0` vs `global=0,secure=0,system=0`) - same value, different membership. The `spoof:<key>` probes compared the whole `store=value` token; they now compare the distinct value(s) only, so a consistent value across stores is consistent while a real injection (value on one path, absent on another) still diverges. Same logic `spoof:sweep` already used.

## [2.16] - 2026-09-13

- **Settings spoof consistency, read by every path.** The spoof-consistency probes read each spoofed key (`ad_aaid`, `android_id`, `device_name`, `bluetooth_name`, the accessibility/developer toggles, `mock_location`, `oem_unlock_allowed`, ...) through every in-process route at once - `Settings.*.getString`, `ContentResolver.query` by appended path / by selection / by bulk sweep, and `call(GET_<store>)` across global/secure/system - with the unhooked shell `settings get` as reference. A value spoofed on one route and not another (`MISMATCH`) is a route-switch tell.

- **Spoofed keys, the ad-id family and spoofed properties come from a central hub, not the binary.** An optional root layer can drop the spoof matrix, the advertising-id key family and the spoofed-property list into the app's `filesDir` (readable where `/data/adb` is not), replacing the built-in default; the built-ins stay as fallback.
- **INT settings are also read through `getInt`, not only `getString`.** A spoofer can hook `getInt` without `getString` (XPL-EX-style modules hook them separately); INT-typed keys are now read through `getInt` across the three stores next to the `getString` sweep. Types come from the central matrix.
- **The validators now cover what a root layer typically spoofs.** 38 system properties gained a cross-method validator; the advertising-id family (`ad_aaid` and siblings) is read through all five property/settings routes; six more HMA-spoofed settings keys joined the matrix.

- **Spoof consistency went UNIVERSAL: every setting, not just our matrix.** `spoof:sweep` enumerates each store (`global`/`secure`/`system`) and, for every key the provider exposes, compares the cached API value (`Settings.*.getString`, what hooks target) against the raw provider `query` - any key whose two doors disagree is a spoof covering one and not the other. Plus the injection case: a key served by the API but absent from the provider in all three stores (the `ad_aaid` pattern), cross-store so a "moved" setting (e.g. `adb_enabled`) is not mistaken for one. ~26 s, cheaper than the old per-key matrix; the curated per-key probes stay as the full-8-path detail for high-value keys.

- **The device's own firmware props are the ruler: bootimage partition added.** The `init_boot`/`boot` ramdisk carries its own build identity (`ro.bootimage.build.id`, `ro.product.bootimage.model/name`), so a fingerprint/model spoofer that rewrites `ro.build.*`/`ro.product.*` but forgets this partition diverges. 18 firmware props were added, each read by getprop / native read_callback / native property_get / shell (`ro.bootimage.build.*`, `ro.product.bootimage.*`, `ro.mot.build.guid`, `ro.hardware.soc.manufacturer`, `ro.build.ab_update`, `ro.treble.enabled`, `ro.boot.dynamic_partitions`); SELinux-restricted ones come back null with all lenses agreeing (no false positive). The bootimage product variant joins the `dev:model/brand/...` family view as non-voting context.

- **The advertising / anonymous-device IDs of Chinese OEMs are read now too.** The MSA-alliance identifiers (OAID, VAID, AAID, UDID) on Xiaomi/MIUI and similar are read two ways each - `com.android.id.impl.IdProviderImpl` by reflection and the `com.miui.idprovider` provider - and crossed, so a spoof covering one path stands out. Null without the MSA provider (e.g. Motorola). `UserManager.getUserCreationTime` is read too.

- **Structural property-area tamper, not just the value.** `integrity:prop_area_holes` reads the SHAPE of the property store: bionic keeps each SELinux context's properties as a bump-allocated trie (a fresh area is one contiguous run), and a `resetprop`/injection whose value no longer fits its slot strands aligned holes the trie no longer points at. It walks every area already mapped (from `/proc/self/maps`, no `opendir`/re-open SELinux would gate) and reports `AVAILABLE`, `CONTEXTS`, `HOLES`. A spoof clean by every getter still shows its footprint here; build-time cloning reports 0 holes.

- **The process's supplementary groups are surfaced (AID_READPROC).** The running process's group set is read three ways (`/proc/self/status` file / native / shell) and crossed; gid 3009 (`AID_READPROC`) means the app can read other processes' `/proc` - a privilege an ordinary app never holds, the key the LSPosed Privisolated mount-view check uses.

- **Two builds now, one variable: the target SDK.** A `compat` flavor (applicationId suffix `.compat`) targets SDK 27 (`untrusted_app_27` domain), the default `modern` targets 35 (strict `untrusted_app`); comparing their snapshots isolates what the platform gates on target SDK. `self:targetSdk` and `selinux:attr_current` print the target and domain, and the launcher icon badges the number. On a Motorola Android 16, target 27 restored package visibility (509 vs 482 packages) but not `ip`/netlink or `/sys/class/net` - so there the netlink lockdown is the ROM's SELinux policy, not the target SDK.
- **The target SDK is shown in the header and About dialog**, read at runtime, so the build states its sandbox.
- **The catalogue of root, hiding and detection apps was widened.** The root-manager set gained current Magisk and KernelSU forks; three sets were added (apps requiring root; root/emulator/Play Integrity detectors; remote-access/ssh/root file managers) plus root-adjacent tools. Each is checked in-process (`getPackageInfo`) against `pm list packages`, matched by exact name.
- **A false divergence on long properties is gone.** The 92-byte `__system_property_get` reports `TOO_LONG_FOR_92B_API` for a value past its buffer (a long fingerprint); that now counts as an instrument limit, shown but not voting.
- **The device serial reads the same on every target.** `Build.getSerial()` throws for target Q+ (a refusal) but degrades to literal "unknown" for P-; that "unknown" is normalised to the same refusal token, so the serial no longer diverges on the compat build.
- **The installed-package shell check is portable across ROMs.** It passed patterns as repeated `-e` flags, which some `grep` builds honour only for the first; it passes them as one newline-separated argument to `grep -Fx` now.
- **A filter that leaves a single list opens it, and a single item expands it**, so a
  narrow filter shows its content without another tap.

## [2.15]

- **A dead string, for the third time, so the build enforces it now.** Removing a
  probe kept leaving its title behind - a text declared in both languages that
  nothing ever shows, which is work handed to whoever translates the app next. The
  written rule ("audit the strings in the same pass") failed three times because it
  is a rule someone has to remember; lint fails on an unused resource now.
  The guard is enforced by lint, and was validated by planting an unused string
  and confirming the build fails on it - a guard that does not fire grants
  confidence without protection.
  On its first run it caught a real defect: the manifest declared
  `android:roundIcon="@mipmap/ic_launcher"`, pointing the ROUND icon at the square
  one, so launchers asking for the round variant got the square and the round asset
  travelled dead inside the APK.
- **Final audit, ten dimensions, source and device.** Two things it caught. A probe
  had gone redundant: once `id:android_id` carried the full settings block, the
  separate provider probe was a partial-coverage twin of a complete one, so its
  unique reading (the provider driven from a command line) moved in and the twin
  went. And a broken instrument was still voting: on `ime:all` the popen twin
  answered `EXIT:255` and took part in the verdict while its JVM sibling had
  silently dropped the same failure - the failure semantics had been aligned for
  errors the SHELL reports and not for the ones the native side reports itself.
  Everything else was already at zero: no property or settings key read through a
  partial set of doors, no duplicated reading, no `read_callback` without its
  `__system_property_get`, no disagreement between the two shell routes beyond a
  timeout that votes on nothing. 777 probes, 3522 readings, and the only divergences
  left on either device are the genuine ones.
- **Everything `/proc/self` exposes that nothing was reading.** 22 probes, each
  through every door that reaches it - the Java file API, `readlink`/`open` from
  JNI, `android.system.Os` (the Java door to the same syscall) and the shell.
  The namespaces are the sharpest: a process confined to its own MOUNT namespace
  sees a filesystem nobody else sees, which is precisely how Magisk and KernelSU
  hide their mounts from an app - and BOTH test devices show it, the app's `mnt`
  sitting outside the range the other namespaces use. Plus the real executable, the
  filesystem root (a root that is not `/` means chroot), the kernel's own account of
  this process's privileges (seccomp mode, NoNewPrivs, effective and bounding
  capabilities, uid/gid/groups), the SELinux context from `/proc/self/attr/current`
  as a fourth route beside `id -Z`, the cgroup, and the architecture claimed by
  three different layers. `readlink` had been implemented in the JNI layer and wired
  to nothing; it now has four consumers.
  Three defects, all caught by measuring: the shell readings were
  reading THE WRONG PROCESS (`/proc/self` in a shell is the shell - `proc:exe`
  answered `/system/bin/toybox`, the readlink binary describing itself, and the
  thread count came from `awk`); `File.canonicalPath` resolves the PATH and never
  the link target, so it answered `/proc/27822/ns/mnt` where the others answered
  `mnt:[4026535778]`; and the architecture came in two vocabularies (`arm64-v8a`
  from the ABI, `aarch64` from the kernel). With the pid named, hidepid refuses
  those shell readings - which is the honest answer, and they are kept visible as
  context because a device where the shell CAN read another process is the anomaly.
- **Readings that used one libc door and not the other.** bionic has two entry
  points into the property store and the app is meant to use both - the 92-byte one
  is what shipped a placeholder as a value in 2.13. Ten probes had only the callback
  side, and a further gap surfaced: the audit script matched
  `sysprop("literal-key")` and was blind to every call that passes the key as a
  VARIABLE, which is how the 24 Build fields, the radio properties and a whole
  helper read theirs. All paired now, and the check no longer depends on the key
  being spelled out - it counts the calls to each function, and the snapshot is
  checked directly for probes carrying one door without the other. Zero, on both
  devices, 3435 readings.
- **The reading rows were breaking the list apart.** A long source took the whole
  row and squeezed the trailing context chip to its minimum width, which rendered
  the word ONE LETTER PER LINE - a tall column of characters that pushed everything
  around it. The source takes the flexible space now and wraps; the chip keeps its
  size. The labels that grew today were trimmed too (the longest went from 91
  characters to 77), and the popen reading stopped repeating the key its siblings
  omit.
- **The two shell routes disagreed 41 times about refusals they had both suffered.**
  Chasing a mislabelled `settings get` uncovered a chain of them, each hiding the
  next. The label named a command form that does not exist (`settings get <key>`
  without a namespace is an `Invalid namespace` error), so it now says
  `settings get {global,secure,system} <key>`, which is what runs. Then the refusals
  themselves: `settings` and `content` print sentences that read like content, and
  `content` prints a whole Java stack trace WITH THE PID IN IT - two runs, two pids,
  two texts, and the routes "diverged" over a refusal both had received. Both are
  refusal tokens now. And `popen` was losing stderr: appending `2>&1` binds the
  redirect to the LAST process of a pipeline, so everything before it escaped
  uncaptured while the JVM route captured it all through redirectErrorStream; the
  command is grouped now. 41 disagreements down to 2, and those two are instrument
  failures, which do not vote.
- **The settings keys had the same one-sided gap as the properties, in a worse
  place.** Six keys were read through one or two doors while the `set:` items got
  the full treatment - the three stores, the provider query and the shell. Worst of
  them: `android_id`, the identifier a privacy app is opened for, was split across
  two probes and NEITHER was complete - one had the three stores without the
  provider, the other the provider without the stores. And the incomplete keys were
  `advertising_id`, `limit_ad_tracking`, `mock_location`, `http_proxy` and
  `enabled_input_methods`: precisely the ones somebody rewrites. Asking for a
  settings key now returns every door to it, the same way a property does.
- **Duplicated readings, and a hole I opened inside the fix for holes.** Converting
  the semantic probes to the shared property block replaced the Java line and left
  the native one standing next to it, so seven probes read the same key twice
  through the same lens - the four attestation items, `id:imei`, and `cpu:abi` and
  `net:hostname` with `getprop` in duplicate. Worse: when the block was given a
  normaliser it DROPPED the shell routes instead of applying the normaliser to
  them, which left the four attestation probes - the ones that compare the TEE
  against the property - with no `getprop` at all. The property was checked against
  the TEE through the Java and native doors only: the exact "rigorous here, casual
  there" hole this block exists to close, reintroduced inside the block itself. The
  command runs the same now and its output goes through the normaliser in Kotlin,
  like every other route. `attest:os_patch` is six readings: the TEE plus five
  property routes, none repeated.
- **The app was rigorous where the data is anonymous and casual where it is
  sensitive.** Cross-checking every probe against every other by what each reading
  actually touches found 13 properties read exhaustively in one place and through a
  single door in another - and the single door was always the Java one, the layer a
  hook rewrites. `prop:ro.serialno` was read five ways while `id:serial`, the probe
  a person opens to find out whether they are exposed, reached for
  `ril.serialnumber` with a lone `SystemProperties.get`. Same for `id:imei`
  (`ril.imei`), `id:iccid`, the four attestation probes, `cpu:abi`, `net:hostname`,
  `sys:selinux` and `hw:low_ram`. A well-made spoofer would have had the app lying
  on the identity screen and telling the truth on the properties screen.
  Rather than patch the 13, asking for a property now returns all of its doors at
  once - the same lesson as pairing the shell routes in the constructor. The four
  attestation probes fold the property into the TEE's vocabulary before comparing
  ("green" to "verified", a date to YYYYMM), and that normalisation used to reach
  only the Java reading; the block applies it to every route, or the other three
  would "diverge" over notation - the classic false positive of comparing the
  same fact written two different ways.
  The file dimension was checked too and had one gap: `/proc/meminfo` was read three
  ways in one probe and two in another. `id:serial` now carries 17 readings,
  `id:imei` 14, and a full scan is 3331.
- **Alternative routes added wherever an honest one exists.** Going through the 110
  single-reading probes: the core count now answers four ways (the JVM, libc through
  `Os.sysconf`, and the kernel's own `/sys/devices/system/cpu/present` and `nproc`);
  `/proc/meminfo` gained the native and shell readings and a comparable
  `hw:mem_total_kb` beside it; the root, hook, hiding and analysis package lists are
  asked BOTH through `getPackageInfo` in this process and through `pm list packages`
  from another one, which is the pair that catches a framework hiding a package
  in-process; and this app's own `versionCode` and `targetSdk` are read from the
  installed record AND parsed back out of the APK file on disk, so rewriting what
  PackageManager says about us would mean rewriting the file too. 101 probes still
  read one way, and they are the ones with no second source that is not invented:
  the app ops, the intent-resolution queries, the attestation record, the sensor and
  camera enumerations, `AccountManager`, `CallLog`, `MediaDrm` and the dynamic
  telephony states.
- **Four of those additions were wrong on the first try, and the device said so.**
  `nproc` answered 4 where the device has 8 - it honours this process's CPU affinity
  and Android confines an app to a cpuset, so it answers "how many may I use?", a
  different and genuinely interesting question, now shown without voting. The whole
  of `/proc/meminfo` is dynamic - MemFree moves between readings - so comparing the
  text diverged on every scan. The APK-on-disk reading silently inherited the
  `compare=false` of the dumpsys reading next to it, leaving the probe with a single
  voter, which was the very thing being fixed. And a probe comparing the framework's
  interface list against `/sys/class/net` was removed: an app cannot list that
  directory, so the comparison would be permanently one-sided.
  That last one also exposed the pipeline trap AGAIN, in the half I had not
  fixed: `ls /denied | sort | paste` exits 0 because the status is the last
  command's, so ls's complaint flowed down the pipe and was taken for data - the
  same shape that once reported "cat/proc/self/cmdline" as a package name. The
  shell's error SHAPE is now rejected as a value in `Exec.run` too, not only in
  `runOrReason`.
- **Three false divergences reported by users, all from the same mistake: comparing
  readings that do not answer the same question, or do not speak the same language.**
  `Hostname` compared `net.hostname` - the name the device ANNOUNCES to the network,
  which vendors fill with the model - against the kernel's own hostname, which is
  `localhost` on every Android; it diverged on every device whose vendor sets the
  property. `SELinux enforce` showed `1`, `1`, `1`, `Enforcing`, `enforcing`: the
  same fact in three vocabularies, so `getenforce` is folded into the file's
  notation and the two finally compare - a `getenforce` that disagreed with `/sys`
  is now a real finding instead of a hidden one. And the attested OS version came
  out as `0.0.15` against `Build.VERSION.RELEASE` = `15`: the record encodes MMmmss
  (150000 = Android 15) but some vendors write the bare major, and dividing that by
  10000 invented a disagreement where the TEE and the framework agreed completely.
  Neither test device exercises this fix - both report proper MMmmss - so it is
  verified by walking the function through its cases, not by a device. Said plainly
  because "verified on both devices" would have been false here.
- **A refusal now keeps the sentence behind the token.** `NOT_PERMITTED` next to
  another lens's `NOT_PERMITTED` is what makes the agreement visible, so the token
  has to be short - but the system's own words carry the diagnosis, and "The uid
  10337 does not meet the requirements to access device identifiers" names both the
  uid and the rule. The token compares, the sentence is kept as a detail under it,
  and it travels in the shared text digest too - that digest is what people paste
  into a message, and `NOT_PERMITTED` alone does not say which uid was refused by
  which rule.
  The two test devices show exactly why the split is needed: Android 16 says
  "The uid 10458" and Android 12 says "The user 14122" - identical refusals, wording
  that differs by version. Comparing the sentences would have measured the wording.
- **The divergence counter uses a real plural** instead of "divergence(s)".
- **Every shell command is now run both ways, by construction.** Pairing them at
  each call site left 668 readings through the JVM against 464 through popen: 204
  commands asked only through `ProcessBuilder`, the surface a root-hiding framework
  rewrites. The invariant moved into `probe()`, so the next person to add a probe
  cannot forget it - a `Method` carries its command now, which is what makes the
  twin buildable. 3268 readings, 668 on each shell route, none unpaired.
  And the pairing immediately earned its keep: four probes started diverging on
  both devices because the JVM route normalised a refusal to `EACCES` while the
  native twin passed through "cat: /proc/version: Permission denied". The routes
  agreed completely about the device and disagreed only about wording - the
  normalisation lived inside `runOrReason`, which only the JVM route calls. Two
  lenses answering the same question must answer in the same vocabulary, or the
  comparison measures the translator instead of the fact. A full scan costs about 37s and 31s on the two test devices, up from 28s and 21s.
- **31 dead string resources removed.** Declared in both languages and referenced by
  no code: 26 `p_*` (friendly names for properties, from before the probes started
  using the property key itself as the title) and 5 `i_*` (integrity titles that
  moved to the `t_*` set). They broke nothing, but they were not harmless either -
  anyone translating the app into a third language would have translated 31 texts
  that never reach the screen.
- **A refusal and a broken instrument are not the same thing.** `EACCES`, `DENIED`,
  `NOT_PERMITTED` and `absent` are answers ABOUT THE DEVICE - it was asked and it
  said no, which is exactly what conformance looks like, so they must be compared.
  `EXIT:n`, `SIGNAL:n`, `TIMEOUT`, `NO_STATUS` and `ERR(...)` say something else:
  "I could not find out". A reading that did not find out no longer votes on the
  verdict, though it stays on screen, because a route that stops working is worth
  seeing. This was already doing damage: three environment probes were reported as
  divergent while three lenses agreed on the value and only the popen route said
  `EXIT:-1`. And that token was manufactured: `pclose` returns -1 when it cannot
  reap the child, which happens inside a JVM because the runtime has its own SIGCHLD
  handling. The native side no longer claims a status it does not have.
- **The lens names were hiding the very distinction that gives them value.** A shell
  command run by the JVM and the same command run through popen are not the same
  vantage point - one goes through `ProcessBuilder`, the surface a root-hiding
  framework rewrites, and the other does not. They are now `JVM+SH` and `JNI+SH`,
  and popen readings have their own lens instead of being filed under `JNI` next to
  direct syscalls.
- **Six things the system services answer and this app never asked**: whether a VPN
  is carrying its traffic (the framework's view against the tunnel interfaces, four
  routes), the HTTP proxy, fake location providers, a debugger attached (framework
  against the kernel's TracerPid), whether a lock screen is really set, the sensor
  vendors and input devices that give an emulator away, and the nine app ops this
  package holds. `loc:test_providers` first mixed three different questions into
  one probe - is a fake provider installed, may THIS app mock location, and a legacy
  setting - the same mistake as comparing `/sys/fs/selinux/enforce` with
  `ro.boot.selinux`. Only the first one votes.
- **`android.system.Os`: the Java door to the syscalls the native lens already
  calls.** Not a repetition - a hook on the framework wrapper does not touch libc,
  and one on libc does not touch this. Added next to the existing readings for the
  pid, this app's uid, `/data` size, the hostname and the root artefact paths, which
  now have three routes to the same question (libc `access()`, `Os.stat()`, and the
  shell). The environment variables gained it too, and there the pair is pointed:
  `System.getenv` returns a COPY cached when the VM started, `Os.getenv` asks libc
  now. The reference lists `Os.gethostname()` as public and the compiler
  disagrees - it lives in libcore, not the SDK; `uname().nodename` is the public
  door to the same kernel answer.
- **Four questions the framework answers and this app never asked**: running in a
  test harness, in a user test harness, driven by Monkey, and low-RAM device. The
  first and the last have a property behind them, so they are comparisons rather
  than lone statements.
- **Five routes to every system property.** `SystemProperties.get` (Java), bionic's
  `__system_property_read_callback`, bionic's OTHER entry point
  `__system_property_get` (the 92-byte one whose placeholder shipped as a value in
  2.13, now reported as its own token), `getprop` through the JVM, and `getprop`
  through popen without the JVM. The two libc functions are different code with
  different contracts, and the two shell routes are not hooked the same way, so a
  framework that rewrites one and not the others shows up as a disagreement.
  This was first limited to the "sensitive" properties to save forks, which was
  wrong twice: `sensitive` means "mask in the UI", not "a spoofer cares", so
  `ro.build.fingerprint` and `ro.boot.verifiedbootstate` - the two most rewritten
  properties on a device - got one route FEWER than an unset IMEI property. And the
  fork is not the expensive part: popen averages 0.27s per reading against 0.47s for
  the JVM route, so the cheaper route was the one being rationed. A full scan is
  3002 readings in 28 seconds.
- **Shell commands ran through the JVM only, which is the one place a hook lives.**
  `ProcessBuilder`/`Runtime.exec` is among the most hooked surfaces on Android: a
  framework hiding root from an app rewrites it there, in Java. Asking only through
  that route is asking exactly where the lie is told. A native route was added -
  `popen()` from JNI, which forks and execs without ever touching the hooked class -
  and the twelve commands where a lie would pay are now run both ways: the root
  artefact paths, the `su` binary, `which su`, the injected-library grep over
  `/proc/self/maps`, the mount markers, the SELinux context, TracerPid and the
  verified boot state. A disagreement between the two IS the hook. Measured on both
  devices, including one with Magisk: they agree, which is what conformance looks
  like - and it is now a measurement instead of an assumption.
- `grep` exits 1 when nothing matches, so a process with no injected libraries
  reported a silent null: the absence was never stated. It says `absent` now.
- The divergence counter uses a real plural resource instead of "(s)".
- **The `content` command was never asked anything.** Providers were read through
  the Java ContentResolver only; the other shell route into the same store did not
  exist in any probe. It does answer an app - with "Error while accessing provider",
  because it reaches the store through `getContentProviderExternal`, which needs
  shell/root - so the refusal is now on screen as the conformant reading, and a
  device that answers is the anomaly. Added as ONE conformance probe plus a reading
  on the Android ID: a single `content query` costs 2-3 seconds from inside an
  app, and chaining the three settings stores blew the timeout, reporting TIMEOUT 22
  times and hiding the real answer. The `gservices` provider stays out - the command
  cannot reach it even as root, only the Java lens can.
- **A placeholder is not an answer.** `Build.SERIAL` is hardcoded to the literal
  "unknown" for any app targeting O+ - the framework saying "you do not get this",
  the same disguise as the old `__system_property_get` placeholder. Read raw it
  contradicted `getSerial()`, which refuses out loud, and the probe reported a
  divergence where the two actually AGREE: the app is denied the serial. Both
  normalise to the one refusal token now, so a conformant device matches and only a
  device that hands the serial over stands out. Scoped to that one reading on
  purpose: `unknown` is the genuine content of `ro.bootloader` and `ro.carrier` on
  some devices, where all three lenses agree on it - normalising by value instead of
  by origin would have turned 18 true readings into false refusals.
- **Two false divergences that only a second device could show.** The ABI lists were
  joined with `", "` on the Java side and with `","` in the property - formatting,
  not disagreement, and invisible on a device with a single ABI. And `sys:selinux`
  was mixing two different questions: `/sys/fs/selinux/enforce` is the running
  state, `ro.boot.selinux` is what the bootloader was told. They only looked
  comparable while the file readings were silently empty; once the refusal became a
  value, an `EACCES` started "contradicting" an `enforcing` that was never the same
  fact. The boot property is context now.
- **Telephony and self-package probes stopped being read one way only.** The radio
  properties hold the same facts the TelephonyManager reports, so operator, MCC+MNC,
  country ISO, roaming and SIM count are compared against them through the native
  and shell lenses; SIM state, network type and baseband are shown as context
  because the two speak different vocabularies for the same thing. those
  properties carry ONE VALUE PER SIM SLOT (`72423,`, `false,false`,
  `LOADED,NOT_READY`) while the API answers for the default subscription, so both
  new lenses keep the first slot - comparing raw would have flagged all ten.
  This app's own package name, uid, and the packages sharing that uid now compare
  against `/proc/<pid>/cmdline`, `getuid()`, `id -u` and `pm list packages --uid`.
- **A pipeline's exit status is the last command's, and that reported an error as a
  value.** `cat /proc/N/cmdline | tr -d '\0'` exits 0 when `cat` fails because `tr`
  succeeded, and with stderr merged the message arrives looking exactly like
  content: the app reported `cat: /proc/14101/cmdline: No such file or directory` as
  its own package name. The shell's error SHAPE (`cmd: subject: reason`) is now
  recognised whatever the exit status claims.
- The 46 telephony and self-package titles became string resources; they had escaped
  the earlier i18n pass because they go through a helper rather than the probe
  builders directly.

- **The refusal is the reading, everywhere.** A permission failure used to land in
  the ERROR bucket, which reads as "the app broke" and left the interesting case
  with nothing to compare against: a device that DOES hand an ordinary app the IMEI
  had no divergence to show. Permission-shaped failures now become a value
  (`NOT_PERMITTED`, `EACCES`, `DENIED`, `EPERM`, `ENOENT`); a genuine defect still
  reports as an error. Sixteen probes moved out of ERROR on the test device -
  IMEI/IMSI/ICCID/MEID, cell info, line number, `/proc/version`, `/proc/stat`,
  `/sys/fs/selinux/*` and the restricted settings keys - and `Build.SERIAL` vs
  `Build.getSerial()` was revealed as a real divergence: the framework answers
  `unknown` through one path and refuses through the other.
- **All three lenses now speak the same refusal.** Java reporting `EACCES` while
  the native and shell lenses stayed silent on the SAME denied file was worse than
  either extreme: it looks like a comparison and is not one. `/proc/version`,
  `/proc/stat`, `/sys/fs/selinux/enforce` and `policyvers` agree across the three.
- **Build fields are no longer read one way only.** All 24 fields with a canonical
  system property gained the native and shell readings - a `Build.*` constant is
  initialised from a property, so the property is the second, independent way to
  read the same thing, and the pair is what catches a spoof applied to one side.
- **Three shell lenses were dead and reported nothing.** `ls -d` over the root
  artefact paths exits non-zero as soon as ONE path is missing, which is always, so
  the shell reading of the root artefacts never produced anything (0 of 6 probes);
  it now answers `absent`, the same word the native lens uses. `ip link` (0 of 4)
  and `getenforce` (0 of 1) are refused to an untrusted app and now show that
  refusal instead of an empty cell.
- `settings get` is shown but never compared: it reaches the provider through
  `getContentProviderExternal`, which needs shell/root, so an app is refused every
  time (22 of 22 measured). It stays visible because a device that answers there
  handed an ordinary app a shell-level privilege.
- Installed-application count gained the `pm list packages` reading.

## [2.14]

- **The refusal is an answer too.** Surfaces the sandbox is meant to close are now
  probed on purpose, because `EACCES` is the conformant reading: a device that hands
  the content over instead is out of conformance and you want to see it. 19 items
  (eMMC CID/serial, UFS and SoC identity, `/proc/cmdline`, `/proc/stat`, `/proc/1/*`,
  `/dev/kmsg`, `/system/build.prop`) plus 22 `dumpsys` services. To compare a refusal
  it has to be a value, so the native lens gained `__system_property`-style errno
  reporting (`EACCES`/`ENOENT`/...), the Java lens normalises the exception, and the
  shell lens folds stderr in and maps it to the same token. `dumpsys` refuses in two
  shapes and both are normalised: `DENIED` (missing the DUMP permission) and
  `NO_SERVICE` (servicemanager does not even expose the service to an app).
- **Advertising ID actually reads now.** It was blank because it was read from the
  settings stores, which is not where the GAID lives: it comes from a bound Play
  Services service. Reimplemented with a raw binder transaction, no Play dependency.
  The whole key family is swept as well (AAID, plus the OAID/VAID of the Chinese OEM
  alliance and Huawei's `pps_*`) across the three settings stores.
- **Every settings item is now read twice**, through the cached `Settings.*` API that
  hooking frameworks rewrite and through the ContentResolver query that goes to the
  store itself. A disagreement means the API is lying to the app.
- **System features** are compared properly: the full set from
  `getSystemAvailableFeatures()` against `pm list features` (another process asking
  the same PackageManager), with the declaring `etc/permissions/*.xml` shown as
  context because it legitimately differs. Plus 12 per-feature items.
- **OEM identifier properties and recovered settings keys**: 77 OEM identifier properties (Meizu,
  Oppo, OnePlus, Asus, TCT, Verizon and the IMEI/MEID/ICCID/serial/MAC variants), and
  13 settings keys the rewrite had dropped, including the hidden-api-policy trio (a
  tamper tell) and the accessibility keys (an accessibility service can read the
  screen). `READ_GSERVICES` and `AD_ID` are declared: both are protectionLevel
  "normal", granted at install with no prompt, so what they unlock is squarely inside
  what any installed app can read about you.
- **x86** added to the ABIs (x86_64 was already there), so the app runs on 32 bit
  emulators too.
- Fixed three false divergences where the two lenses were answering different
  questions: mount markers (a summary against raw mountinfo lines, and "overlay" as a
  loose substring when OverlayFS is ordinary on Android), `which su` (a PATH walk
  against one hardcoded path) and enabled input methods (the framework's filtered view
  against the raw setting).
- **A fourth false divergence, and the worst of them: the app was inventing root.**
  The mount-marker probe scanned the whole `/proc/self/mountinfo` line for `ksu`, and
  every ext4 line carries the mount option `journal_checksum`, which contains that
  substring. A clean Samsung reported KernelSU. The probe now reads only the mount
  point, the filesystem type and the source, never the options, in both lenses.
- **Every probe title is translatable.** The 172 titles were literals in the probe
  builders; they are string resources now, resolved at build time so an exported
  report still carries readable prose.
- **GSF ID knocks on both doors.** On Android 16 the `gservices` database moved from
  `com.google.android.gsf` to the Play Services provider, so the old authority
  answers empty on a device that does have the identifier. Both are queried.
- **The snapshot and the shared report are written atomically.** The UI scan and the
  background worker each held their own store and wrote the same file at the same
  time; each stream truncates on open but then writes from zero independently, so the
  shorter payload landed inside the longer one and the tail of the longer one survived
  past its end. The result was a file with a complete JSON object followed by garbage,
  which only showed on a device slow enough for the two to overlap. The writers are
  serialised now and each write goes through a temporary file renamed into place.
- Fixed two platform-type NPEs (the GSF ID cursor and the network interface
  enumeration, both declared non-null by Kotlin and both nullable in practice).
- **Search shows what it finds.** A match inside a collapsed section stayed hidden;
  sections now open while a query is active. Plus a clear button in the search field
  and a larger version string in the header.

## [2.13]

- **Fixed a native reading that could report a libc error message as a value.** The
  native lens used `__system_property_get()`, whose buffer is 92 bytes
  (`PROP_VALUE_MAX`). For a property longer than that, bionic refuses to truncate and
  writes the literal string `Must use __system_property_read_callback() to read`
  into the buffer. That placeholder is an error message, not a value: it was shown
  as the native reading and diverged from the Java and shell ones, a false mismatch
  on any device with a long property. The native lens now reads through
  `__system_property_find()` + `__system_property_read_callback()`, which has no
  length limit, and every label was updated to name the function actually used.
  Verified against a 309 character property: all three lenses now return it whole.

## [2.12]

- **About dialog made readable.** It opened with a dense paragraph of description and
  everything else in small type. The description is gone and the type is larger
  (rows and links at body-large, section titles bold), so what is left is only what
  matters: project, contacts and download.

## [2.11]

- **About dialog.** Who wrote the app, where the source lives, the licence, and the
  contacts (Telegram, e-mail, XDA, GitHub), one tap away in the top bar. The 2.x
  rewrite had dropped this, so the information existed only in the README.
- **An unexpected error now opens a copyable dialog instead of killing the app.**
  The uncaught-exception handler writes the trace to the app's private files, the
  app relaunches, and the next start shows the full stack trace in a scrollable
  dialog with a copy button, so it can be pasted into a report. A guard stops a
  start-up failure from looping the relaunch. Nothing is transmitted: there is no
  crash reporting service and the app holds no INTERNET permission, so the report
  only leaves the device if you copy it out yourself. The dialog also links straight
  to the contacts, so the report can actually be sent somewhere.
- Native readings in the attestation items now name the property they read
  (`__system_property_get ro.boot.verifiedbootstate` instead of a bare
  `__system_property_get`), matching how the Java readings are labelled.

## [2.10]

- **Fewer false positives on identity items.** The verdict now compares only readings
  of the *same* field. Each `Build.*` is compared against its canonical property
  (`ro.product.model`, `ro.build.fingerprint` ...) through Java/native/shell; the
  per-partition variants (`ro.product.vendor.*`, `.system.*`, `.odm.*`), which Android
  deliberately lets differ, stay visible but are marked as "context" and no longer
  raise a MISMATCH. The TEE tag only joins the comparison when it follows the same
  naming convention as `Build.*` (brand/device/manufacturer); for model/product it
  uses the codename, so it is shown as context only. Model, product and fingerprint
  are back to MATCH, while a real `Build.*` hook (diverging from the canonical
  property) is still caught and the TEE security-patch divergence stays red.
- **Fixed a crash when expanding a category.** A property present both in the raw
  catalog and in a domain module that re-categorises it (emulator markers, integrity
  flags, DNS, GPU) produced two probes sharing one id; the duplicate lazy-list key
  crashed the screen as soon as both were drawn, which showed up first on the
  Emulator section. The registry now keeps a single reading per id (the domain one,
  which carries the more meaningful category).

## [2.09]

- **Deeper SELinux, input methods, and a native second opinion.** New comparison
  items, each still read through several methods: SELinux context (`/proc/self/attr/current`
  via Java, native open/read, and `id -Z`), policy version and MLS flag, the enabled
  and installed input methods (IMM, Settings.Secure, `settings`/`ime`), and boot time
  (`/proc/stat btime`). TracerPid and mount markers (magisk/overlay/ksu) are read once
  through the JVM/File and again through the native raw `open()`/`read()` so a
  Java-only hook of `/proc` files diverges from the native reading.
- **More surfaces per item.** Serial now also reads `androidboot.serialno` from
  `/proc/cmdline`; MAC adds an `ip link` reading alongside sysfs and native; install
  source adds `updateOwnerPackageName` (API 34).

## [2.08]

- Closed the gap against COPG/PIF-style Zygisk spoofers: `Build.VERSION.*` (release,
  SDK int, codename, incremental, security patch) and build time are now compared
  against their `ro.build.version.*` / `ro.build.date.utc` properties (they were
  single-method before), so a `SetStaticObjectField` spoof of those fields shows up.

## [2.07]

- **TEE / attestation lens.** Generates a hardware-attested KeyStore key and parses
  the key-attestation record (verified boot state, device-locked, patch levels,
  root-of-trust, and - when supported - brand/device/model from the secure
  hardware), placing each next to the property/Build reading. The attestation record
  is signed by the TEE, so a spoofer that rewrites Build.* and properties cannot
  rewrite it: any mismatch is a strong tell. Offline only (no network).

## [2.06]

- **Tail collectors** ported for full parity with 1.x: "this package" self-inspection
  through PackageManager (uid, gids, installer, install source, install/update times,
  target SDK, enabled/instant/suspended state, whitelisted restricted permissions,
  module info, own Xposed metadata...) and the intent-resolution enumerations
  (`queryIntentActivities`/`Services`/`BroadcastReceivers`/`ContentProviders`,
  `resolveActivity`, `queryInstrumentation`), plus `/dev/ptmx` and `stat`. ~577 items.

## [2.05]

- **Bulk collectors**: full installed-package list, per-app signing digests, and
  running services/processes (the last two return only this app on modern Android -
  itself informative).
- App header now shows the version dynamically (from `BuildConfig`), so it always
  matches the build.
- **Package renamed** to `ru.vd171.vdinfos`.
- README's "What it inspects" now carries the full coverage (a separate coverage
  document was folded in and removed).

## [2.04]

- Wider device-identity/hardware surface, each read through every method:
  SubscriptionManager (multi-SIM: iccid/number/carrier/mcc/mnc/country per sub),
  primary IMEI, user serial, Android ID via the secure provider, cell info,
  cameras, system features, and more Settings keys (wifi_mac, device_name,
  boot_count, adb_enabled, development_settings_enabled, data_roaming, http_proxy).

## [2.03]

- **Method debugger.** Reworked so each information item is read through *every*
  method that can read it (e.g. `Build.SERIAL` / `Build.getSerial()` /
  `SystemProperties` / `getprop` / native), compared side by side; each reading
  shows its exact source. Every system property is read three ways
  (SystemProperties / native / getprop).
- **Wide coverage**: device identity, identifiers, the TelephonyManager surface,
  network (per-interface MAC, Wi-Fi, Bluetooth, DNS), kernel/process/system,
  hardware (CPU/memory/sensors/display/GPU), Widevine DRM, packages, accounts,
  WebView, and expanded root/hook/emulator detectors.
- **UI** grouped into collapsible per-category sections with counts, defaulting open
  where a divergence exists; results stream in as the scan runs.
- **Engine** hardened: the shell lens can never stall the scan (bounded,
  kill-then-read), probes run on the IO dispatcher with a per-probe timeout, and
  results are no longer dropped when the buffer is full.

## [2.02]

- **Save to file** via the Storage Access Framework: a modern document picker
  where you choose the folder and can rename the file (a name is only suggested).
  The previous share sheet stays as a separate action.
- **Screen capture policy probe** (category *Security*): surfaces
  `DevicePolicyManager.getScreenCaptureDisabled`, the value that FLAG_SECURE /
  screenshot-unblocking modules typically force. Java-lens only, since there is no
  native/syscall counterpart to cross-check it against.
- Typography: purged em/en dashes from the whole source tree and added a guard so
  they cannot creep back in.
- Docs: unified the legacy GitHub README/LEIAME with the 2.x READMEs (English and
  pt-BR); moved the root-hiding and root-detection lists into a dedicated catalog
  (`CATALOG.md`); refreshed the contacts and download sections.

## [2.01]

- **Bilingual app**: every user-facing string moved to resources - English default
  (`values/`) plus pt-BR (`values-pt/`); the app follows the device language.
- **Bilingual docs**: `README.md` (English) and `README.pt.md`.
- **License**: GNU **AGPL-3.0-or-later** (was MIT). Copyleft with the network
  clause, chosen to keep forks of a research/anti-detection tool open.

## [2.00] - rewrite

A ground-up rewrite around a single idea: read every fact twice (Java SDK vs
native/JNI) and surface the divergences. Divergence means a likely hook
(XPrivacyLua / Xposed), which was the original 1.x use case.

### Added
- **Dual-lens verification.** Every probe is read through the Java SDK *and*
  through libc/syscalls via JNI; a **verdict** flags where they disagree.
- **Native lens (JNI).** `__system_property_get`, `uname(2)`, raw `open()/read()`
  of `/proc` and `/sys`, per-interface MAC from `/sys/class/net`, `getuid`,
  `statvfs`, and an `access()`-based root/hook artifact scan - no shell process.
- **Parallelism.** ~380 probes fan out over coroutines with bounded concurrency
  and stream into the UI as they complete (was a single serial `AsyncTask`).
- **Background action.** A WorkManager job re-scans on a schedule, diffs against
  the previous capture, and notifies when any value drifts between runs.
- **Modern UI.** Jetpack Compose + Material 3, dynamic colour, dark/light, live
  progress, search, category filters, "only divergent" view, PII masking, and
  JSON/text export.

### Changed
- Kotlin + Compose replace Java + `ExpandableListView`.
- `minSdk` 21 -> 26, `targetSdk` 35, Gradle Kotlin DSL + version catalog.
- The ~360-property catalog from 1.x is preserved as pure data.
- Dropped string obfuscation (lsparanoid): the source is meant to be readable.

---

## 1.x (legacy, Java)

The 1.x series was a single-Activity Java app that dumped device information into
an expandable list, used to debug XPrivacyLua. Full history and APKs live on the
[GitHub releases page](https://github.com/VD171/VD-Infos/releases).

### [1.11-beta6] - 2024-12-02

Last public 1.x release. Highlights:

- Documented how each piece of info is gathered; split reading into sections with
  per-section elapsed time.
- Removed the root detector, toybox binaries, and Memory info.
- Added a lot of Android 13 (TIRAMISU) content.
- Added a VPN-state detector and a simple LSPosed detector.
- Added export of configs to XPL-EX (XPrivacyLua Extended).
- Added WebView info, Location info, TimeZone/Locale info, GPU info, DRM ID,
  Boot ID, and keychain-file stat info.
- Added the whole PackageManager and TelephonyManager surfaces, Connection and
  network info, and Environment info.
- Added Google/Android/Amazon advertising IDs and raw GSF ID.
- Added CPU info (`cat` and file), Bluetooth name, app signatures, and a
  "This Package" view showing how the app sees itself.
- More Build/SIM/system props; simpler exception and build-date handling.

### 1.10 and earlier - 2024-03-22 and before

`1.10`, `1.09`, `1.08`, `1.06` and the rest: see the
[releases page](https://github.com/VD171/VD-Infos/releases) for their notes and
signed APKs.

---

## Contacts

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **E-mail:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **GitHub:** @VD171 https://github.com/VD171
