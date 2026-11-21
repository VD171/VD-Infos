## What

Describe what this PR changes and why.

## Checklist

- [ ] The AGPL banner header is present on every new/changed source file.
- [ ] Code and comments are in English; no em/en-dashes; zero body comments (identifiers explain the code).
- [ ] No internal module, infra, or codename is referenced in public code or comments.
- [ ] If a UI label changed, every `values-*/strings.xml` and `README.*.md` was updated in step.
- [ ] If a probe changed, detection was not weakened; an unreadable source returns a non-voting token, never a false MISMATCH.
- [ ] Both flavors build (`./gradlew assembleRelease`) and `lintModernRelease` is clean.
- [ ] Tested on a rooted device (note which device/ROM/root).

## Notes

Anything reviewers should know.
