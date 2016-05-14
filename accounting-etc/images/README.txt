Notes on exporting icons for various operating systems:

##########
# Windows
##########
Export each of the "icon_xx.xcf" into a bitmap using Alpha-Channel RGB 32 bitmap color depth (make sure to select the compatibility option "Do not write color space information" and the advanced option "32 bits A8 R8 G8 B8":

accounting_16.bmp
accounting_32.bmp
accounting_48.bmp
accounting_256.bmp

Open the 16, 32 and 48 bitmaps in MS Paint and save them as 8-bit bmps:

accounting_16_8.bmp
accounting_32_8.bmp
accounting_48_8.bmp

Using ImageMagick command line tools, convert all of the above into a single ico file:

<path_to_image_magick>/convert accounting_16.bmp accounting_16_8.bmp accounting_32.bmp accounting_48.bmp accounting_256.bmp accounting_16_8.bmp accounting_32_8.bmp accounting_48_8.bmp -compress None accounting.ico

Replace the resulting ico file in accounting-product/icons

########
# Linux
########

Open icon_256.xcf in Gimp - select "File/Export As..." and choose "../accounting-product/incons/icon.xpm" as the destination. Done.

##########
# MacOs X
##########

1) Create a folder named "accounting.iconset"
2) Export the following images as PNG into the folder:
    icon_16x16.png
    icon_16x16@2x.png (Resolution 32x32)
    icon_32x32.png
    icon_32x32@2x.png (64x64)
    icon_128x128.png
    icon_128x128@2x.png (256x256)
    icon_256x256.png
    icon_256x256@2x.png (512x512)
    icon_512x512.png
    icon_512x512@2x.png (1024x1024)
3) Open a terminal, run "iconutil -c icns -o accounting.icns <iconset filename>" where <iconset_filename> is the folder you exported the PNGs into
4) Copy the resulting file accounting.icns to accounting-products/icons

Further info:
http://andrius.velykis.lt/2012/10/creating-icons-for-eclipse-rcp-launcher/
https://developer.apple.com/library/mac/documentation/GraphicsAnimation/Conceptual/HighResolutionOSX/Optimizing/Optimizing.html#//apple_ref/doc/uid/TP40012302-CH7-SW3