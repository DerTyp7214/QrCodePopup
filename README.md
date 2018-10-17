[![](https://jitpack.io/v/DerTyp7214/QrCodePopup.svg)](https://jitpack.io/#DerTyp7214/QrCodePopup)

# QrCodePopup

## To import the lib


### 1. Add it in your root build.gradle at the end of repositories
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### 2. Add the dependency:
```gralde
dependencies {
    implementation 'com.github.DerTyp7214:QrCodePopup:<LATEST_VERSION>'
}
```

## Usage

### Functions

Requirement Level | Function
---------|-----------------------------
**must** | `new QRCodeDialog(<Activity>)`
**must** | `show(<Content>)`
_optional_ | `customImageTint(<Bitmap>)`

### Example

```java
QRCodeDialog qrCodeDialog = new QRCodeDialog(<Activity>);
qrCodeDialog.customImageTint(<Bitmap>);
qrCodeDialog.show(<Content>);
```
