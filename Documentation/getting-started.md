## Getting started

The Adobe Experience Platform Identity for Edge Network extension has the following peer dependency, which must be installed prior to installing the identity extension:
- [Mobile Core](https://github.com/adobe/aepsdk-core-android)

## Configure the Identity extension in Data Collection UI
1. Log into [Adobe Experience Platform Data Collection](https://experience.adobe.com/data-collection).
2. From **Tags**, locate or search for your Tag mobile property.
3. In your mobile property, select **Extensions** tab.
4. On the **Catalog** tab, locate or search for the **Identity** extension, and select **Install**.
5. There are no configuration settings for **Identity**.
6. Follow the [publishing process](https://developer.adobe.com/client-sdks/documentation/getting-started/create-a-mobile-property) to update SDK configuration.

## Add the Adobe Experience Identity extension to your app

### Download and import the Identity extension

> **Note** The following instructions are for configuring an application using Adobe Experience Platform Edge mobile extensions. If an application will include both Edge Network and Adobe Solution extensions, both the Identity for Edge Network and Identity for Experience Cloud ID Service extensions are required. Find more details in the [Frequently Asked Questions](frequently-asked-questions.md) page.

1. Add the Mobile Core, Edge, and Edge Identity extensions to your project using the app's Gradle file:

   ```java
   implementation 'com.adobe.marketing.mobile:core:2.+'
   implementation 'com.adobe.marketing.mobile:edge:2.+'
   implementation 'com.adobe.marketing.mobile:edgeidentity:2.+'
   ```
> **Warning**
> Using dynamic dependency versions is not recommended for production apps. Refer to this [page](https://github.com/adobe/aepsdk-core-android/blob/main/Documentation/GradleDependencies.md) for managing gradle dependencies.

2. Import the Mobile Core and Edge extensions in your Application class.
   ```java
    import com.adobe.marketing.mobile.MobileCore;
    import com.adobe.marketing.mobile.Edge;
    import com.adobe.marketing.mobile.edge.identity.Identity;
   ```

### Register the Identity for Edge Extension with MobileCore

#### Java

```java
public class MobileApp extends Application {
    // Set up the preferred Environment File ID from your mobile property configured in Data Collection UI
    private final String ENVIRONMENT_FILE_ID = "";

    @Override
    public void onCreate() {
      super.onCreate();
      MobileCore.setApplication(this);
      MobileCore.configureWithAppID(ENVIRONMENT_FILE_ID);

      // Register Adobe Experience Platform SDK extensions
      MobileCore.registerExtensions(
         Arrays.asList(Edge.EXTENSION, Identity.EXTENSION),
         o -> Log.debug("MobileApp", "MobileApp", "Adobe Experience Platform Mobile SDK initialized.")
       );
    }
}
```
#### Kotlin

```kotlin
class MobileApp : Application() {
    // Set up the preferred Environment File ID from your mobile property configured in Data Collection UI
    private var ENVIRONMENT_FILE_ID: String = ""
    override fun onCreate() {
        super.onCreate()
        MobileCore.setApplication(this)
        MobileCore.configureWithAppID(ENVIRONMENT_FILE_ID)
        // Register Adobe Experience Platform SDK extensions
        MobileCore.registerExtensions(
            listOf(Edge.EXTENSION, Identity.EXTENSION)
        ) {
            Log.debug("MobileApp", "MobileApp", "Adobe Experience Platform Mobile SDK initialized.")
        }
    }
}
```
