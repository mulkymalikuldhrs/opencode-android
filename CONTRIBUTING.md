# Contributing to OpenCode Android

First off, thank you for considering contributing to **OpenCode Android**! It's people like you who make this project a great tool for the community. This document provides guidelines and instructions for contributing to the project.

---

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Setup](#development-setup)
- [Coding Standards](#coding-standards)
- [Submitting Changes](#submitting-changes)
- [Reporting Bugs](#reporting-bugs)
- [Suggesting Features](#suggesting-features)
- [Community](#community)

---

## Code of Conduct

This project and everyone participating in it is governed by basic principles of respect and professionalism. By participating, you are expected to uphold this standard. Please be respectful, constructive, and inclusive in all interactions. Harassment, discrimination, or abusive behavior will not be tolerated.

---

## How Can I Contribute?

There are many ways to contribute to OpenCode Android, and not all of them involve writing code:

### Reporting Bugs
If you find a bug, please open an issue on our [GitHub Issues](https://github.com/mulkymalikuldhrs/opencode-android/issues) page. Provide as much detail as possible, including your device model, Android version, steps to reproduce, and expected vs. actual behavior.

### Suggesting Enhancements
We welcome feature suggestions! Open an issue with the label `enhancement` and describe the feature you would like to see, why it would be useful, and any implementation ideas you may have.

### Writing Code
Pull requests are welcome for bug fixes, new features, and improvements. Please follow the coding standards and submission process outlined below.

### Improving Documentation
Documentation improvements are always welcome. Whether it's fixing a typo, adding examples, or writing new guides, documentation contributions help everyone.

### Translating
Help make OpenCode Android accessible to more people by translating the app and documentation into additional languages. We currently support English, Bahasa Indonesia, and Chinese, and we welcome additional translations.

---

## Development Setup

### Prerequisites

1. **Android Studio** (latest stable version recommended)
2. **Android SDK** with API level 24+ (Android 7.0)
3. **Kotlin** knowledge (the project is 100% Kotlin)
4. **Git** for version control
5. **OpenCode Server** running locally for testing (install via Termux or PC)

### Getting the Code

```bash
# Fork the repository on GitHub
# Then clone your fork
git clone https://github.com/YOUR_USERNAME/opencode-android.git
cd opencode-android

# Add the upstream remote
git remote add upstream https://github.com/mulkymalikuldhrs/opencode-android.git
```

### Building the Project

1. Open the project in Android Studio
2. Let Gradle sync and download dependencies
3. Build the project: `Build > Make Project`
4. Run on a device or emulator: `Run > Run 'app'`

### Setting Up the Backend

For full functionality testing, you need an OpenCode server running:

```bash
# Using Termux on Android
pkg update -y
pkg install nodejs-lts -y
npm i -g opencode-ai
opencode serve --port 4096

# Using PC/Mac/Linux
npm install -g opencode-ai
opencode serve --port 4096
```

Then connect the app to `http://<your-ip>:4096` in the Connection Wizard.

---

## Coding Standards

### Kotlin Style

- Follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Keep functions focused and concise
- Prefer immutable data structures (`val` over `var`)
- Use data classes for model objects
- Add KDoc comments for public APIs

### Architecture

- Follow the existing client-server architecture pattern
- API calls should go through `OpenCodeClient` or `OpenCodeApi`
- Use Kotlin coroutines for asynchronous operations
- Use `StateFlow` for reactive state management
- Keep UI logic in Fragments and business logic in manager classes

### Code Formatting

- Use 4-space indentation (no tabs)
- Maximum line length of 120 characters
- Add blank lines between logical sections
- Organize imports alphabetically

### Commit Messages

Follow the conventional commits format:

```
type(scope): description

[optional body]

[optional footer]
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Build process or auxiliary tool changes

Examples:
```
feat(chat): add markdown rendering for messages
fix(terminal): resolve crash on command execution
docs(api): update endpoint documentation
refactor(client): simplify HTTP request builder
```

---

## Submitting Changes

### Pull Request Process

1. **Create a branch** for your changes:
   ```bash
   git checkout -b feat/your-feature-name
   ```

2. **Make your changes** following the coding standards above.

3. **Test your changes** thoroughly:
   - Test on at least one physical device or emulator
   - Verify the app builds without warnings
   - Test with and without an OpenCode server connection
   - Check for memory leaks and performance regressions

4. **Commit your changes** with clear, descriptive commit messages.

5. **Push to your fork:**
   ```bash
   git push origin feat/your-feature-name
   ```

6. **Open a Pull Request** against the `main` branch of the upstream repository.

7. **Describe your changes** in the PR description:
   - What does this PR do?
   - Why is it needed?
   - How was it tested?
   - Any breaking changes?

### Pull Request Review

- A maintainer will review your PR as soon as possible
- Be responsive to feedback and requested changes
- Keep the PR focused on a single change or feature
- Resolve merge conflicts before requesting review

---

## Reporting Bugs

When filing a bug report, please include the following information:

1. **Device Information:** Phone model, Android version, ROM
2. **App Version:** The version of OpenCode Android you are running
3. **Server Version:** The version of OpenCode server you are connected to
4. **Steps to Reproduce:** Clear, numbered steps
5. **Expected Behavior:** What you expected to happen
6. **Actual Behavior:** What actually happened
7. **Logs/Screenshots:** If applicable, attach logcat output or screenshots

Submit bugs to: [https://github.com/mulkymalikuldhrs/opencode-android/issues](https://github.com/mulkymalikuldhrs/opencode-android/issues)

---

## Suggesting Features

Feature requests are welcome! Please provide:

1. **Problem Statement:** What problem does this feature solve?
2. **Proposed Solution:** How should it work?
3. **Alternatives Considered:** Other approaches you have thought about
4. **Additional Context:** Screenshots, mockups, or references to similar features in other apps

Submit feature requests to: [https://github.com/mulkymalikuldhrs/opencode-android/discussions](https://github.com/mulkymalikuldhrs/opencode-android/discussions)

---

## Community

- **GitHub:** [https://github.com/mulkymalikuldhrs/opencode-android](https://github.com/mulkymalikuldhrs/opencode-android)
- **Issues:** [https://github.com/mulkymalikuldhrs/opencode-android/issues](https://github.com/mulkymalikuldhrs/opencode-android/issues)
- **Discussions:** [https://github.com/mulkymalikuldhrs/opencode-android/discussions](https://github.com/mulkymalikuldhrs/opencode-android/discussions)
- **Related Project:** [HermesQuantOS](https://github.com/mulkymalikuldhrs/HermesQuantOS)

---

## Contact

**Mulky Malikul Dhaher**
- Email: mulkymalikuldhrs@email.com
- GitHub: [https://github.com/mulkymalikuldhrs](https://github.com/mulkymalikuldhrs)

Thank you for contributing to OpenCode Android! Your efforts help make AI-powered coding accessible on Android devices everywhere.

---

**⚠️ For Education Purpose Only** — This project is provided strictly for educational and research purposes. The authors and contributors assume **no responsibility or liability** for any damages, losses, or risks arising from the use of this software. **We do not bear any responsibility or risk** for how this software is used.
