/*
  Copyright 2021 Adobe. All rights reserved.
  This file is licensed to you under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under
  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
*/

package com.adobe.marketing.edge.identity.app.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.adobe.marketing.edge.identity.app.R
import com.adobe.marketing.edge.identity.app.model.SharedViewModel
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.edge.identity.AuthenticatedState
import com.adobe.marketing.mobile.edge.identity.Identity
import com.adobe.marketing.mobile.edge.identity.IdentityItem
import com.adobe.marketing.mobile.edge.identity.IdentityMap
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CustomIdentityFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedViewModel by activityViewModels<SharedViewModel>()

        // Instantiates a layout XML file (compiled only!) into its corresponding View objects.
        // In this case, inflating the custom identity fragment (read: component)
        val root = inflater.inflate(R.layout.fragment_custom_identity, container, false)

        // Then find the real views (inflated above) using their resource IDs
        val adIdEditText = root.findViewById<EditText>(R.id.text_ad_id)
        // Set the text in the textbox equal to the real value stored in local state
        adIdEditText.setText(sharedViewModel.adId.value)
        // When a text change is detected (after change is completed)
        adIdEditText.doAfterTextChanged {
            // Each text value store has its own setter
            sharedViewModel.setAdId(it.toString())
        }

        val identifierEditText = root.findViewById<EditText>(R.id.text_identifier)
        identifierEditText.setText(sharedViewModel.identifier.value)
        identifierEditText.doAfterTextChanged {
            sharedViewModel.setIdentifier(it.toString())
        }

        val namespaceEditText = root.findViewById<EditText>(R.id.text_namespace)
        namespaceEditText.setText(sharedViewModel.namespace.value)
        namespaceEditText.doAfterTextChanged {
            sharedViewModel.setNamespace(it.toString())
        }

        val isPrimaryCheckbox = root.findViewById<CheckBox>(R.id.checkbox_is_primary)
        isPrimaryCheckbox.isChecked = sharedViewModel.isPrimary.value ?: false
        isPrimaryCheckbox.setOnCheckedChangeListener { _, isChecked ->
            sharedViewModel.setIsPrimary(isChecked)
        }

        val authenticatedRadioGroup = root.findViewById<RadioGroup>(R.id.radio_group_authenticated)
        sharedViewModel.authenticatedStateId.value?.let { checkedId ->
            authenticatedRadioGroup.findViewById<RadioButton>(checkedId).isChecked = true
        }
        authenticatedRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val changedRadioButton: RadioButton = group.findViewById(checkedId)
            if (changedRadioButton.isChecked) {
                sharedViewModel.setAuthenticatedState(AuthenticatedState.fromString(changedRadioButton.text.toString()))
                sharedViewModel.setAuthenticatedStateId(checkedId)
            }
        }

        root.findViewById<Button>(R.id.btn_update_identities).setOnClickListener {
            val identifier: String? = sharedViewModel.identifier.value
            val namespace: String? = sharedViewModel.namespace.value
            val authenticatedState: AuthenticatedState? = sharedViewModel.authenticatedState.value
            val isPrimary: Boolean = sharedViewModel.isPrimary.value ?: false

            val item = IdentityItem(identifier, authenticatedState, isPrimary)
            val map = IdentityMap()
            map.addItem(item, namespace)
            Identity.updateIdentities(map)
        }

        root.findViewById<Button>(R.id.btn_remove_identities).setOnClickListener {
            val identifier: String? = sharedViewModel.identifier.value
            val namespace: String? = sharedViewModel.namespace.value
            val authenticatedState: AuthenticatedState? = sharedViewModel.authenticatedState.value
            val isPrimary: Boolean = sharedViewModel.isPrimary.value ?: false

            val item = IdentityItem(identifier, authenticatedState, isPrimary)
            Identity.removeIdentity(item, namespace)
        }

        // Button for Set Ad ID behavior
        // Sets the advertising identifier using the MobileCore API
        // This only sets the ad ID provided, it does not incorporate the request tracking authorization
        // flow; that is handled separately
        root.findViewById<Button>(R.id.btn_set_ad_id).setOnClickListener {
            val adId: String? = sharedViewModel.adId.value

            MobileCore.setAdvertisingIdentifier(adId)
        }

        // In Android, users are automatically opted-in to ad ID tracking
        // They can choose to opt out of tracking in Android Settings at the device(?) level (not sure if
        // individual app level permissions are possible)
        // thus developers using ad ID should get ad ID from the API each time it is used, as permissions can change
        // - note: users can see their ad ID value in the settings page for Ads
        // - note: unlike iOS, the permission is more passive, where the user has to seek out the option to turn
        // off tracking, and no permissions prompt is shown on first launch, etc.
        //

        // Accessibility of ad ID through AdvertisingIdClient APIs:
        // All devices that support Google Play Services follow the device level opt-in/out status, regardless of the app’s target SDK level.
        // This is enforced starting from April 1, 2022.
        // Prior to this, it was only applied to devices running Android 12.

        // Required manifest permissions to use ad ID (normal level permission):
        // This is only required for Android 13+ (Apps with target API level set to 33 (Android 13))
        // Conversely, for apps with target API level set to 32 (Android 12L) or older, this permission is not needed.
        // If it is not declared when required, you will get an all-zero ad ID.
        // In the test app's case, the current targetSdkVersion is 30, so the permission is not required.
        //
        // The permission is:
        // <uses-permission android:name="com.google.android.gms.permission.AD_ID"/>
        // To prevent permission merging:
        // <uses-permission android:name="com.google.android.gms.permission.AD_ID" tools:node="remove"/>
        //
        // note: some SDKs may already include this permission in their manifest; ex: Google Mobile Ads SDK (play-services-ads)
        // consider merging manifest files: https://developer.android.com/studio/build/manage-manifests#merge-manifests

        // Based on simulator testing:
        // - the opt-in/out status change or resetting the ad ID value does not affect app lifecycle;
        // that is, unlike iOS the app is not terminated with value or opt-in/out status changes
        //     - thus, the guidance for only accessing the ad ID through the API and not caching the value;
        //     permissions for ad tracking and/or the value of the ID itself may be changed at any time
        //     - practically, it may be cumbersome to detect changes at arbitrary points in the logic throughout the app;
        //     it may be helpful to use:
        //         - a getter helper for ad ID that detects and handles changes in value or opt-in/out
        //         - using app lifecycle foreground event to check for ad ID value or opt-in/out changes
        // - opt-out does not seem to cause the admob SDK to return an all-zeros ad ID (based on testing with emulator)
        //

        // Google Mobile Ads Lite SDK
        // https://developers.google.com/admob/android/lite-sdk
        // API reference: https://developers.google.com/android/reference/com/google/android/gms/ads/identifier/AdvertisingIdClient
        // AdMob requires an application ID (free sample id given by Google for testing purposes) specified in the AndroidManifest.xml
        // for the app to run, when the SDK is included in the build (otherwise the app will crash).
        // However, the SDK doesn't have to be initialized in order to use the ad ID fetching flow:
        // https://developers.google.com/admob/android/quick-start#import_the_mobile_ads_sdk

        // AndroidX Ads SDK
        // https://developer.android.com/jetpack/androidx/releases/ads#1.0.0-alpha04
        // API reference: https://developer.android.com/reference/androidx/ads/identifier/AdvertisingIdClient
        // Doesn't work (also see commented implementation below):
        // https://stackoverflow.com/questions/59217195/how-do-i-use-or-implement-an-android-advertising-id-provider
        // Note: the last time the AndroidX SDK was updated was January 22, 2020: Version 1.0.0-alpha04

        root.findViewById<Button>(R.id.btn_get_gaid).setOnClickListener {
            val context = context
            Log.d("Custom_Identity_Fragment", "context: $context, appContext: ${context?.applicationContext}")
            if (context != null) {
                // Implementation for google play services ads
                // Logic implemented in ViewModel to leverage managed Kotlin coroutine scope from lifecycle-viewmodel-ktx
                // lifecycle-viewmodel-ktx requires compiled sdk version 31+
                GlobalScope.launch {
                    sharedViewModel.getGAID(context.applicationContext)
                }

                /* Implementation for AndroidX ads, doesn't seem to work; isAdvertisingIdProviderAvailable never returns true even with valid applicationContext
                if (AdvertisingIdClient.isAdvertisingIdProviderAvailable(context.applicationContext)) {
                    val advertisingIdInfoListenableFuture = AdvertisingIdClient.getAdvertisingIdInfo(context.applicationContext)
                    addCallback(advertisingIdInfoListenableFuture,
                            object : FutureCallback<AdvertisingIdInfo> {
                                override fun onSuccess(adInfo: AdvertisingIdInfo?) {
                                    if (adInfo == null) {
                                        return
                                    }
                                    val id = adInfo.id
                                    val providerPackageName = adInfo.providerPackageName
                                    val isLimitTrackingEnabled = adInfo.isLimitAdTrackingEnabled
                                    Log.d("Custom_Identity_Fragment", "id: $id, providerPackageName: $providerPackageName, isLimitTrackingEnabled: $isLimitTrackingEnabled")
                                }

                                override fun onFailure(t: Throwable) {
                                    Log.e("Custom_Identity_Fragment", "Failed to connect to Advertising ID provider: $t")
                                    // Try to connect to the Advertising ID provider again, or fall
                                    // back to an ads solution that doesn't require using the
                                    // Advertising ID library.
                                }
                            },
                            Executors.newSingleThreadExecutor()
                    )
                } else {
                    Log.d("Custom_Identity_Fragment", "The Advertising ID client library is unavailable.")
                    // The Advertising ID client library is unavailable. Use a different
                    // library to perform any required ads use cases.
                }
                 */
            }
        }

        return root
    }
}
