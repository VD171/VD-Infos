# Changelog

All notable changes to VD Infos. Dates are ISO (YYYY-MM-DD).

The 2.x line is a ground-up rewrite; the last public 1.x release was
[v1.11-beta6](https://github.com/VD171/VD-Infos/releases/tag/v1.11-beta6)
(2024-12-02). Everything between it and 2.00 is the rewrite described below.

## [2.15]

- **A dead string, for the third time, so the build enforces it now.** Removing a
  probe kept leaving its title behind - a text declared in both languages that
  nothing ever shows, which is work handed to whoever translates the app next. The
  written rule ("audit the strings in the same pass") failed three times because it
  is a rule someone has to remember; lint fails on an unused resource now.
  🪤 The first attempt at that guard did not fire: a second `lint` block was added
  above the one that already existed, and the later block won. It only counted as a
  guard once an unused string was planted on purpose and the build was watched to
  fail - a guard that does not fire is worse than none, because it grants
  confidence without protection.
  🔑 And it earned itself on its first honest run: the manifest declared
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
  🔑 The namespaces are the sharpest: a process confined to its own MOUNT namespace
  sees a filesystem nobody else sees, which is precisely how Magisk and KernelSU
  hide their mounts from an app - and BOTH test devices show it, the app's `mnt`
  sitting outside the range the other namespaces use. Plus the real executable, the
  filesystem root (a root that is not `/` means chroot), the kernel's own account of
  this process's privileges (seccomp mode, NoNewPrivs, effective and bounding
  capabilities, uid/gid/groups), the SELinux context from `/proc/self/attr/current`
  as a fourth route beside `id -Z`, the cgroup, and the architecture claimed by
  three different layers. `readlink` had been implemented in the JNI layer and wired
  to nothing; it now has four consumers.
  🪤 Three mistakes of mine, all caught by measuring: the shell readings were
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
  side, and then the owner spotted more: the audit script matched
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
  once - the same lesson as pairing the shell routes in the constructor. 🪤 The four
  attestation probes fold the property into the TEE's vocabulary before comparing
  ("green" to "verified", a date to YYYYMM), and that normalisation used to reach
  only the Java reading; the block applies it to every route, or the other three
  would "diverge" over notation - the mistake already made with EACCES, with
  Enforcing and with the ABI lists.
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
  🪤 That last one also exposed the pipeline trap AGAIN, in the half I had not
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
  🪤 Neither test device exercises this fix - both report proper MMmmss - so it is
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
  🔑 The two test devices show exactly why the split is needed: Android 16 says
  "The uid 10458" and Android 12 says "The user 14122" - identical refusals, wording
  that differs by version. Comparing the sentences would have measured the wording.
- **The divergence counter uses a real plural** instead of "divergence(s)".
- **Every shell command is now run both ways, by construction.** Pairing them at
  each call site left 668 readings through the JVM against 464 through popen: 204
  commands asked only through `ProcessBuilder`, the surface a root-hiding framework
  rewrites. The invariant moved into `probe()`, so the next person to add a probe
  cannot forget it - a `Method` carries its command now, which is what makes the
  twin buildable. 3268 readings, 668 on each shell route, none unpaired.
  🔑 And the pairing immediately earned its keep: four probes started diverging on
  both devices because the JVM route normalised a refusal to `EACCES` while the
  native twin passed through "cat: /proc/version: Permission denied". The routes
  agreed completely about the device and disagreed only about wording - the
  normalisation lived inside `runOrReason`, which only the JVM route calls. Two
  lenses answering the same question must answer in the same vocabulary, or the
  comparison measures the translator instead of the fact. A full scan costs 37s on
  the vienna and 31s on the Merlin2, up from 28s and 21s.
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
  `EXIT:-1`. 🪤 And that token was manufactured: `pclose` returns -1 when it cannot
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
  package holds. 🪤 `loc:test_providers` first mixed three different questions into
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
  now. 🪤 The reference lists `Os.gethostname()` as public and the compiler
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
  🪤 This was first limited to the "sensitive" properties to save forks, which was
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
  on the Android ID: 🪤 a single `content query` costs 2-3 seconds from inside an
  app, and chaining the three settings stores blew the timeout, reporting TIMEOUT 22
  times and hiding the real answer. The `gservices` provider stays out - the command
  cannot reach it even as root, only the Java lens can.
- **A placeholder is not an answer.** `Build.SERIAL` is hardcoded to the literal
  "unknown" for any app targeting O+ - the framework saying "you do not get this",
  the same disguise as the old `__system_property_get` placeholder. Read raw it
  contradicted `getSerial()`, which refuses out loud, and the probe reported a
  divergence where the two actually AGREE: the app is denied the serial. Both
  normalise to the one refusal token now, so a conformant device matches and only a
  device that hands the serial over stands out. 🪤 Scoped to that one reading on
  purpose: `unknown` is the genuine content of `ro.bootloader` and `ro.carrier` on
  the Merlin2, where all three lenses agree on it - normalising by value instead of
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
  because the two speak different vocabularies for the same thing. 🪤 those
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
- **Ported from 1.x and from "My Dev IDs"**: 77 OEM identifier properties (Meizu,
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
- **Bilingual docs**: `README.md` (English) and `README.pt-BR.md`.
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
