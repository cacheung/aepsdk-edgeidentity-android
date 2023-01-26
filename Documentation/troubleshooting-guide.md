## Troubleshooting Guides

### M1 issue

M1 Macs may run into errors during the build process, specifically finding the npm installation directory. 

```
Execution failed for task ':spotlessInternalRegisterDependencies'.

Can't automatically determine npm executable and none was specifically supplied!

Spotless tries to find your npm executable automatically. It looks for npm in the following places:
- An executable referenced by the java system property 'npm.exec' - if such a system property exists.
- The environment variable 'NVM_BIN' - if such an environment variable exists.
- The environment variable 'NVM_SYMLINK' - if such an environment variable exists.
- The environment variable 'NODE_PATH' - if such an environment variable exists.
- In your 'PATH' environment variable

If autodiscovery fails for your system, try to set one of the environment variables correctly or
try setting the system property 'npm.exec' in the build process to override autodiscovery.
```

To address this: 
- Update Android Studio to the latest version (minimum version Bumblebee Patch 1 should address this issue) 
- Update the Android Gradle Plugin to the latest version (7.x.x as of this writing)  

If that does not address the issue, try installing node using the installer and not through homebrew: https://nodejs.org/en/download/ 

Please make sure that these build configuration changes are kept local; any build process dependencies (ex: Gradle version, packages) that are updated in this process should **not** be included in any PRs that are not specifically for updating the project's build configuration.