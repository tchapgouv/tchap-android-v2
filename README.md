# Tchap Android

Tchap Android is an Android Matrix Client provided by [DINUM](https://tchap.beta.gouv.fr/). The app can be run on every Android devices with Android OS Lollipop and more (API 21).

[<img src="resources/img/google-play-badge.png" alt="Get it on Google Play" height="60">](https://play.google.com/store/apps/details?id=fr.gouv.tchap.a)

# New Android SDK

Tchap is based on [Element](https://github.com/vector-im/element-android) with a new Android [SDK](https://github.com/matrix-org/matrix-android-sdk2) fully written in Kotlin. In order to make the early development as fast as possible, Tchap and the new SDK currently share the same git repository.

# Releases to app stores

There is some delay between when a release is created and when it appears in the app stores (Google Play Store). Here are some of the reasons:

* Not all versioned releases that appear on GitHub are considered stable. Each release is first considered beta: this continues for at least two days. If the release is stable (no serious issues or crashes are reported), then it is released as a production release in Google Play Store.
* Each release on the Google Play Store undergoes review by Google before it comes out. This can take an unpredictable amount of time. In some cases it has taken several weeks.

If you would like to receive releases more quickly (bearing in mind that they may not be stable) you have a number of options:

1. [Sign up to receive beta releases](https://play.google.com/apps/testing/fr.gouv.tchap.a) via the Google Play Store.
2. Install a [release APK](https://github.com/tchapgouv/tchap-android/releases) directly - download the relevant .apk file. Note: These are not the store versions, so you may have to uninstall the previous Tchap version before. Take care to properly logout and export your encrypted keys before.

## Contributing

Please refer to [CONTRIBUTING.md](https://github.com/tchapgouv/tchap-android-v2/blob/develop/CONTRIBUTING.md) if you want to contribute on the Tchap Android project!

Come chat with the community in the dedicated Matrix [room](https://matrix.to/#/#element-android:matrix.org).

Also [this documentation](./docs/_developer_onboarding.md) can hopefully help developers to start working on the project.

## Triaging issues

Issues are triaged by community members and the Android App Team, following the [triage process](https://github.com/vector-im/element-meta/wiki/Triage-process).

We use [issue labels](https://github.com/vector-im/element-meta/wiki/Issue-labelling) to sort all incoming issues.
