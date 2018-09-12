Author: Steven Dunnington

This program allows images to be encoded with text from a text(.txt) file. This is done by changing the individual bit values of the pixels, in 
a way that encodes the ASCII values of the characters into the image. 

Important notes:
1. Some text files may be too large to encode into a particular image. Larger image files containing more pixels allow for larger text files
to be encoded into the image. 

2. If a text file is particularly large, visible noise may appear in the encoded image. Both the original image and the encoded image will
appear side by side in the interface to allow for a quick comparison, but after saving a fully enlarged image should be inspected to determine the 
amount of visible noise in the image.

3. Encoded images are saved as BMP files, since BMP is a lossless image file format that preserves the exact pixel values.