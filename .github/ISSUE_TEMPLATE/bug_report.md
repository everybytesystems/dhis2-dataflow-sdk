---
name: Bug report
about: Create a report to help us improve
title: '[BUG] '
labels: 'bug'
assignees: ''

---

## Bug Description
A clear and concise description of what the bug is.

## Environment
- **SDK Version**: [e.g., 1.0.0]
- **DHIS2 Version**: [e.g., 2.40.1]
- **Platform**: [e.g., JVM, Android, iOS]
- **Kotlin Version**: [e.g., 1.9.20]
- **OS**: [e.g., macOS 14.0, Ubuntu 22.04]

## Steps to Reproduce
1. Go to '...'
2. Click on '....'
3. Scroll down to '....'
4. See error

## Expected Behavior
A clear and concise description of what you expected to happen.

## Actual Behavior
A clear and concise description of what actually happened.

## Code Sample
```kotlin
// Minimal code sample that reproduces the issue
val client = DHIS2Client(config)
val result = client.tracker.getTrackedEntities()
// Error occurs here
```

## Error Messages
```
Paste any error messages, stack traces, or logs here
```

## Screenshots
If applicable, add screenshots to help explain your problem.

## Additional Context
Add any other context about the problem here.

## Possible Solution
If you have ideas on how to fix the issue, please describe them here.

## Checklist
- [ ] I have searched for existing issues
- [ ] I have provided all required information
- [ ] I have included a minimal code sample
- [ ] I have tested with the latest SDK version