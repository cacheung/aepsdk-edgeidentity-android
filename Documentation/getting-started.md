## Getting started

The Adobe Experience Platform Identity for Edge Network extension has the following peer dependency, which must be installed prior to installing the identity extension:
- [Mobile Core](https://aep-sdks.gitbook.io/docs/foundation-extensions/mobile-core)

## Add the AEP Identity extension to your app

### Download and import the Identity extension

> :information_source: The following instructions are for configuring an application using Adobe Experience Platform Edge mobile extensions. If an application will include both Edge Network and Adobe Solution extensions, both the Identity for Edge Network and Identity for Experience Cloud ID Service extensions are required. Find more details in the [Frequently Asked Questions](https://aep-sdks.gitbook.io/docs/foundation-extensions/identity-for-edge-network/identity-faq) page.


### Java

1. Add the Mobile Core and Edge extensions to your project using the app's Gradle file.
See the [current version list](https://developer.adobe.com/client-sdks/documentation/current-sdk-versions) for the latest extension versions to use.

   ```java
   implementation 'com.adobe.marketing.mobile:core:2.0.0'
   implementation 'com.adobe.marketing.mobile:edge:2.0.0'
   implementation 'com.adobe.marketing.mobile:edgeidentity:2.0.0'
   ```

2. Import the Mobile Core and Edge extensions in your Application class.

   ```java
    import com.adobe.marketing.mobile.MobileCore;
    import com.adobe.marketing.mobile.Edge;
    import com.adobe.marketing.mobile.edge.identity.Identity;
   ```

3. Register the Identity for Edge Extension with MobileCore:

### Java

```java
public class MobileApp extends Application {

    @Override
    public void onCreate() {
      super.onCreate();
      MobileCore.setApplication(this);

      MobileCore.configureWithAppID(ENVIRONMENT_FILE_ID);

      // register Adobe extensions
      MobileCore.registerExtensions(
         Arrays.asList(Edge.EXTENSION, Identity.EXTENSION),
         o -> Log.d("MobileApp", "Mobile SDK was initialized")
       );
    }
}
```
### Kotlin

```kotlin
class MobileApp : Application() {
    // Set up the preferred Environment File ID from your mobile property configured in Data Collection UI
    private var ENVIRONMENT_FILE_ID: String = ""

    override fun onCreate() {
        super.onCreate()

        // register AEP SDK extensions
        MobileCore.setApplication(this)
         
        MobileCore.registerExtensions(
            listOf(Edge.EXTENSION, Identity.EXTENSION, Consent.EXTENSION)
        ) {
            MobileCore.configureWithAppID(ENVIRONMENT_FILE_ID)
        }
    }
}
```
