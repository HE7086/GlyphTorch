# GlyphTorch
This is a very simple app allowing to use the Glyph interface as torch for Nothing Phone (1).

Also provide quicksetting tile and shortcut to toggle the torch.

## Important Note: Requires Root
This app relies on read and write to sysfs to work, so root permission is required. Unless official support to access the glyph interface is added, there's no other way. (except for the janky builtin led tester app)

As this is in very early stage, expect there to be crashes and bugs.

<p float="left">
    <img src="assets/app.png" width="30%">
    <img src="assets/quicksettings.png" width="30%">
    <img src="assets/shortcut.png" width="30%">
</p>

## Note for Nothing OS 2.0
Since Nothing OS 2.0 a functionality is added where long press the flashlight toggle in control center enables glyph as flashlight. However this does not work for homescreen and lockscreen shortcuts (yet).
