import java.awt.image.BufferedImage;

/*
 * Class: Functions.java
 * Purpose:
 * This class contains two methods, encode and decode. These two
 * methods perform the actual encoding of text into an image, and the decoding
 * of text from an image.
 */
public class Functions
{
	public static BufferedImage encode(BufferedImage img, String input) //used to encode an image with a String input
	{
		int width = img.getWidth(); 
		int height = img.getHeight();
		int numPixels = width * height; //number of pixels in the image
		
		input += (char)3; //append the end of text character to the end of the string, which has an ASCII value of 3
		int length = input.length(); //length of the String input
		float bitsNeeded = length * 7; //the basic ASCII set is used, which uses 7 bits per character, thus we need length * 7 total bits to encode the string
		
		if(bitsNeeded > numPixels*24 - 1) //the 24 comes from 8*3, 3 colors (red, green, blue) and 8 bits are used for each color
		{
			//not enough pixels in the image to encode the message
			return null;
		}
		
		//compute the number of bits per pixel that we will be using to encode the String
		int bitsPerPixel = (int) Math.ceil(bitsNeeded / numPixels); 

		
		int redBits = 0;
		int greenBits = 0;
		int blueBits = 0;
		//cycle through and ensure that no bit color has a difference of more than 1 with any other color
		for(int modCounter = 0; modCounter < bitsPerPixel; modCounter++)
		{
			if(modCounter % 3 == 0)
				redBits++;
			else if(modCounter % 3 == 1)
				greenBits++;
			else if(modCounter % 3 == 2)
				blueBits++;
		}
		
	
		
		int stringBitCounter = 0; //total number of bits that have been encoded
		int currentStringBit = 0; //current bit for the string
		
		int newPixelValue = 0; //new pixel value to be used
		int currentPixelBit = 0; //current bit for the pixel value
		int currentPixel; //current pixel that is being encoded
		
		//NOTE: the pixel at (0,0) is exclusively used to encode the number of bits per pixel, this helps make the decoding process easier
		
		//encode the number of bits used per pixel in the blue part of the first pixel at (0, 0)
		currentPixel = img.getRGB(0,  0);
		for(int i = 0; i < 5; i++) //max possible bits per pixel is 24, only need to loop 5 times since 2^5=32, which is greater than 24
		{
			currentStringBit = (bitsPerPixel >> i) & 1; //grabs the current bit of the string (in this case the string is bitsPerPixel)
			currentPixelBit = (currentPixel >> i) & 1; //grabs the current pixel bit
			if(currentStringBit == 0)
			{
				if(currentPixelBit == 1) //pixel bit should be changed to a 0, to match the string bit
				{
					newPixelValue = currentPixel - (1 << i); //1 << i is 2^i
					img.setRGB(0, 0, newPixelValue); //updates the pixel value
					currentPixel = newPixelValue; //updates the currentPixel variable
				}
			}
			else //currentStringBit == 1
			{
				if(currentPixelBit == 0) //pixel bit should be changed to a 1, to match the string bit
				{
					newPixelValue = currentPixel + (1 << i); //1 << b is 2^b
					img.setRGB(0, 0, newPixelValue); //updates the pixel value
					currentPixel = newPixelValue; //updates the currentPixel variable
				}
			}
		}
		
		//encode the String input, skipping the pixel at (0, 0)
		for(int currentHeight = 0; currentHeight < height; currentHeight++) //loop through each row
		{
			for(int currentWidth = 0; currentWidth < width; currentWidth++)  //loop through each column
			{
				if(currentHeight == currentWidth && currentWidth == 0)
				{
					//skip the pixel at (0, 0)
					continue;
				}
				
				currentPixel = img.getRGB(currentWidth, currentHeight);
				
				//red bit loop
				for(int r = 0; r < redBits && stringBitCounter < bitsNeeded; r++) //change the appropriate number of red bits
				{
					currentStringBit = input.charAt(stringBitCounter / 7); //grabs the correct character to be encoded
					currentStringBit = currentStringBit >> (stringBitCounter % 7); //shift right to the correct position of the character
					currentStringBit = currentStringBit & 1; //grabs the individual bit		
					
					currentPixelBit = (currentPixel >> (r + 16)) & 1; //grabs the current pixel bit

					if(currentStringBit == 0)
					{
						if(currentPixelBit == 1) //pixel bit should be changed to a 0, to match the string bit
						{
							newPixelValue = currentPixel - (1 << (r + 16)); //1 << (r + 16) is 2^(r + 16)
							img.setRGB(currentWidth, currentHeight, newPixelValue); //updates the pixel value
							currentPixel = newPixelValue; //updates the currentPixel variable
						}
					}
					else //currentStringBit == 1
					{
						if(currentPixelBit == 0) //pixel bit should be changed to a 1, to match the string bit
						{
							newPixelValue = currentPixel + (1 << (r + 16)); //1 <<(r + 16) is 2^(r + 16)
							img.setRGB(currentWidth, currentHeight, newPixelValue); //updates the pixel value
							currentPixel = newPixelValue; //updates the currentPixel variable
						}
					}
					stringBitCounter++; //increments the stringBitCounter, meaning that 1 additional bit has been encoded
					if(stringBitCounter >= bitsNeeded) //check if whole string has been encoded
					{
						//exits all for-loops, ending the encoding process
						currentWidth += width;
						currentHeight += height;
						break;
					}
				}
				
				//green bit loop
				for(int g = 0; g < greenBits; g++) //change the appropriate number of green bits
				{
					currentStringBit = input.charAt(stringBitCounter / 7); //grabs the correct character to be encoded
					currentStringBit = currentStringBit >> (stringBitCounter % 7); //shift right to the correct position of the character
					currentStringBit = currentStringBit & 1; //grabs the individual bit
					
					currentPixelBit = (currentPixel >> (g + 8)) & 1; //grabs the current pixel bit
					if(currentStringBit == 0)
					{
						if(currentPixelBit == 1) //pixel bit should be changed to a 0, to match the string bit
						{
							newPixelValue = currentPixel - (1 << (g + 8)); //1 << (g + 8) is 2^(g + 8)
							img.setRGB(currentWidth, currentHeight, newPixelValue); //updates the pixel value
							currentPixel = newPixelValue; //updates the currentPixel variable
						}
					}
					else //currentStringBit == 1
					{
						if(currentPixelBit == 0) //pixel bit should be changed to a 1, to match the string bit
						{
							newPixelValue = currentPixel + (1 << (g + 8)); //1 << (g + 8) is 2^(g + 8)
							img.setRGB(currentWidth, currentHeight, newPixelValue); //updates the pixel value
							currentPixel = newPixelValue; //updates the currentPixel variable
						}
					}
					stringBitCounter++;
					if(stringBitCounter >= bitsNeeded) //whole string has been encoded
					{
						//exits all for-loops, ending the encoding process
						currentWidth += width;
						currentHeight += height;
						break;
					}
				}
				
				//blue bit loop
				for(int b = 0; b < blueBits; b++) //change the appropriate number of blue bits
				{
					currentStringBit = input.charAt(stringBitCounter / 7); //grabs the correct character to be encoded
					currentStringBit = currentStringBit >> (stringBitCounter % 7); //shift right to the correct position of the character
					currentStringBit = currentStringBit & 1; //grabs the individual bit
					
					currentPixelBit = (currentPixel >> b) & 1; //grabs the current pixel bit
					if(currentStringBit == 0)
					{
						if(currentPixelBit == 1) //pixel bit should be changed to a 0, to match the string bit
						{
							newPixelValue = currentPixel - (1 << b); //1 << b is 2^b
							img.setRGB(currentWidth, currentHeight, newPixelValue); //updates the pixel value
							currentPixel = newPixelValue; //updates the currentPixel variable
						}
					}
					else //currentStringBit == 1
					{
						if(currentPixelBit == 0) //pixel bit should be changed to a 1, to match the string bit
						{
							newPixelValue = currentPixel + (1 << b); //1 << b is 2^b
							img.setRGB(currentWidth, currentHeight, newPixelValue); //updates the pixel value
							currentPixel = newPixelValue; //updates the currentPixel variable
						}
					}
					stringBitCounter++;
					if(stringBitCounter >= bitsNeeded) //whole string has been encoded
					{
						//exits all for-loops, ending the encoding process
						currentWidth += width;
						currentHeight += height;
						break;
					}
				} //end of blue for-loop
			}//end of inner for-loop
		} //end of outer for-loop
		return img;
	} //end of encode
	
	public static String decode(BufferedImage img)
	{
		StringBuilder sb = new StringBuilder();
		
		int width = img.getWidth();
		int height = img.getHeight();
		
		int currentInt = 0;
		int currentBit;
		
		//get the RGB bits per pixel from (0, 0)
		int firstPixel = img.getRGB(0,  0);
		int bitsPerPixel = 0; //initialized to 0
		for(int i = 0; i < 5; i++)
		{
			currentBit = firstPixel >> i;
			currentBit = currentBit & 1;
			bitsPerPixel += (1 << i) * currentBit;
		}
		
		int redBits = 0;
		int greenBits = 0;
		int blueBits = 0;
		//obtain the RGB bit values using bitsPerPixel
		for(int modCounter = 0; modCounter < bitsPerPixel; modCounter++)
		{
			if(modCounter % 3 == 0)
				redBits++;
			else if(modCounter % 3 == 1)
				greenBits++;
			else if(modCounter % 3 == 2)
				blueBits++;
		}


		
		int currentPixel = 0;
		
		int bitCounter = 0; //counter to determine the placement of each bit, and when the full char has been decoded
		
		for(int currentHeight = 0; currentHeight < height; currentHeight++) //loop through each row
		{
			for(int currentWidth = 0; currentWidth < width; currentWidth++)  //loop through each column
			{
				currentPixel = img.getRGB(currentWidth, currentHeight);
				if(currentHeight == currentWidth && currentWidth == 0)
				{
					//skip the pixel at (0, 0)
					continue;
				}
				for(int r = 0; r < redBits; r++)
				{
					//grab the bit at the correct position
					currentBit = currentPixel >> (16 + r);
					currentBit = currentBit & 1;
					
					//add the bit to the currentInt, using the power of 2
					currentInt += currentBit * (1 << (bitCounter % 7));
					
					bitCounter++;
					if(bitCounter % 7 == 0) //full char has been decoded
					{
						if(currentInt == 3) //end of text has been reached
						{
							//exit loop
							currentWidth += width;
							currentHeight += height;
							break;
						}
						else
						{
							bitCounter = 0;
							sb.append((char) currentInt); //append the char to the StringBuilder

							currentInt = 0; //reset currentInt
						}
					}
				}
				for(int g = 0; g < greenBits; g++)
				{
					//grab the bit at the correct position
					currentBit = currentPixel >> (8 + g);
					currentBit = currentBit & 1;
					
					//add the bit to the currentInt, using the power of 2
					currentInt += currentBit * (1 << (bitCounter % 7));
					
					bitCounter++;
					if(bitCounter % 7 == 0) //full char has been decoded
					{
						if(currentInt == 3) //end of text has been reached
						{
							//exit loop
							currentWidth += width;
							currentHeight += height;
							break;
						}
						else
						{
							bitCounter = 0;
							sb.append((char) currentInt); //append the char to the StringBuilder

							currentInt = 0; //reset currentInt
						}
					}
				}
				for(int b = 0; b < blueBits; b++)
				{
					//grab the bit at the correct position
					currentBit = currentPixel >> b;
					currentBit = currentBit & 1;
					
					//add the bit to the currentInt, using the power of 2
					currentInt += currentBit * (1 << (bitCounter % 7));
					
					bitCounter++;
					if(bitCounter % 7 == 0) //full char has been decoded
					{
						if(currentInt == 3) //end of text has been reached
						{
							//exit loop
							currentWidth += width;
							currentHeight += height;
							break;
						}
						else
						{
							bitCounter = 0;
							sb.append((char) currentInt); //append the char to the StringBuilder

							currentInt = 0; //reset currentInt
						}
					}
				}
			}
		}
		
		return sb.toString();

	} //end of decode
	
}
