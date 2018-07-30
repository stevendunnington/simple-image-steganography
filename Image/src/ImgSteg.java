import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import javax.imageio.ImageIO;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/*
 * Class: ImgSteg.java
 * Purpose:
 * This class contains everything related to the GUI of the application, as well as
 * user input and output of pictures and text files. The main method is contained in this
 * class as well.
 */

public class ImgSteg extends Application
{
	private static BufferedImage openedImage;
	private static boolean hasImage = false; //true if an image has been opened
	private static boolean hasText = false; //true if a text file has been opened
	private static BufferedImage encodedImage;
	private static String inputString;
	private static String decodedString;

	public static void main(String[] args)
	{
		launch(args);
	}
	@Override
    public void start(Stage primaryStage)
	{
        primaryStage.setTitle("Simple Image Steganography");
		
        //window is set to 700x700 pixels
        int xRes = 700;
        int yRes = 700;
        
        BorderPane root = new BorderPane();   
        primaryStage.setScene(new Scene(root, xRes, yRes));
        primaryStage.show();
		
        //various javafx objects are added
		Button instructions = new Button("Instructions");
        
		Button openImgBtn = new Button("Open Image");
		Button saveImgBtn = new Button("Save Image");
		saveImgBtn.setDisable(true); //disable the save image button until an encoded image is ready to be saved
		
		Button openFileBtn = new Button("Open Text File");
		Button saveTextBtn = new Button("Save Text File");
		saveTextBtn.setDisable(true); //disable the save text button until text has been decoded
		
		Button encodeFileBtn = new Button("Encode Text From File");
		encodeFileBtn.setDisable(true); //disable the encode file until both an image and a text file are loaded
		
		Button decodeBtn = new Button("Decode Text From File");
		decodeBtn.setDisable(true); //disable the decode text button until an image has been loaded
		
		Text decodedText = new Text(); //text object to display sample of decoded text
		
		HBox btnBox = new HBox(); //HBox for all of the buttons
		btnBox.getChildren().addAll(openImgBtn, openFileBtn, encodeFileBtn, decodeBtn, saveImgBtn, saveTextBtn, instructions);
		
		Label status = new Label("Open an image or text file.");
		//add the status label on top of the buttons
		VBox botBox = new VBox(); 
		botBox.getChildren().addAll(status, btnBox);
	
		//create image views for the opened image and the encoded image
		ImageView openedImageView = new ImageView();
        openedImageView.setPreserveRatio(true);
        openedImageView.setFitWidth(300); //300 pixels wide
    	ImageView encodedImageView = new ImageView();
        encodedImageView.setPreserveRatio(true);
        encodedImageView.setFitWidth(300);
        
        //add labels to the ImageViews
		Label openedImageLabel = new Label();
		Label encodedImageLabel = new Label();
        VBox openedImgVBox = new VBox();
        openedImgVBox.getChildren().addAll(openedImageView, openedImageLabel);
        VBox encodedImgVBox = new VBox();
        encodedImgVBox.getChildren().addAll(encodedImageView, encodedImageLabel);
        //set the images next to each other in a HBox
        HBox imageBox = new HBox();
        imageBox.getChildren().addAll(openedImgVBox, encodedImgVBox);
        imageBox.setAlignment(Pos.BASELINE_CENTER);
        
        //set the objects to proper positions in the BorderPane
    	root.setBottom(botBox);
    	root.setLeft(decodedText);
		root.setTop(imageBox);

		Alert alert = new Alert(AlertType.INFORMATION); //alert for pop-up information

		//display instructions
		instructions.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent e)
				{
					alert.setTitle("Instructions");
			        alert.setHeaderText(null);
			        String instr = "Open Image: Open an image from the file system. The image can be either an encoded image, or a regular non-encoede image.\n"
			        		+ "Open Text File: Opens a text file with text to be encoded. Supports .txt files.\n"
			        		+"Encode Text From Image: Encodes the text from the text file into the opened image, and displays the "
			        		+ "encoded image.\nDecode Text From Text File: Decodes any encoded text from an image and displays a preview. May decode nonsense if the image has not been encoded with text.\n"
			        		+ "Save Image: Saves a copy of the encoded image as a .bmp file.\n"
			        		+ "Save Text File: Saves a copy of the decoded text as a .txt file.";
			        alert.setContentText(instr);
			        alert.show();
				}
		});
		
		
        //open image
		openImgBtn.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent e)
	        {
				//open image from file explorer
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Image");
				ExtensionFilter filter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.bmp", "*.jpeg", "*.png");
	            fileChooser.getExtensionFilters().add(filter);
				
				try {
					File fileImage = fileChooser.showOpenDialog(primaryStage); 
					openedImage = ImageIO.read(fileImage);
					Image img = SwingFXUtils.toFXImage(openedImage, null);
					openedImageView.setImage(img);
					openedImageLabel.setText("Opened Image");
					
					hasImage = true;
					if(hasText) //if a text file has already been opened, image is ready to be encoded
						encodeFileBtn.setDisable(false); //enables the encode file button
					decodeBtn.setDisable(false);
					status.setText("Image opened successfully."); //update the status
						
				} catch (IOException | NullPointerException e1) {
					hasImage = false; //ensure hasImage is still set to false
					//display alert notifying the user of an error
			        alert.setTitle("File Error");
			        alert.setHeaderText(null);
			        alert.setContentText("Error reading file, please open only standard image file types.");
			        alert.show();
				} catch(IllegalArgumentException e2)
				{
					//results from closing the file chooser, not an error that requires a notification		
				}
				
	        }
		 });

		//open text file
		openFileBtn.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent e)
	        {
				//open text file form file explorer
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Text File");
				ExtensionFilter filter = new FileChooser.ExtensionFilter("TXT File (*.txt)", "*.txt");
	            fileChooser.getExtensionFilters().add(filter);
				
				try {
					File fileImage = fileChooser.showOpenDialog(primaryStage); 
					CharSource source = Files.asCharSource(fileImage, Charsets.UTF_8);
			    	inputString = source.read();
					
					hasText = true;
					if(hasImage) //if an image has already been opened, image is ready to be encoded
						encodeFileBtn.setDisable(false); //enables the encode file button					
					status.setText("Text file opened successfully."); //update the status
					
				} catch (IOException e1) {
					hasText = false;
					//display alert notifying the user of an error
					alert.setTitle("File Error");
				    alert.setHeaderText(null);
				    alert.setContentText("Error reading file, please open only text file types.");
				    alert.show();
				} catch(IllegalArgumentException | NullPointerException e2)
				{
					//results from closing the file chooser, not an error that requires a notification
				}
	        }
		});
		
		//encode text from file into image
		encodeFileBtn.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent e)
	        {
				encodedImage = Functions.encode(openedImage, inputString); //get the encoded image
				if(encodedImage != null)
				{
					saveImgBtn.setDisable(false); //image is ready to be saved
					
					//display the image
					Image img = SwingFXUtils.toFXImage(encodedImage, null);
					encodedImageView.setImage(img);
					encodedImageLabel.setText("Encoded Image");
					
					status.setText("Text encoded successfully."); //update the status
				}
				else //returned image is null, thus the text file was too large to be encoded into the image
				{
					//display alert notifying the user of an error
					alert.setTitle("Encoding Error");
				    alert.setHeaderText(null);
				    alert.setContentText("Text file it too large to be encoded into the opened image. Please try opening a smaller text file or a larger image.");
				    alert.show();
				}
	        }
		});
		
		//save encoded image
		saveImgBtn.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent e)
	        {
				//save image using the file explorer
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Save Image");
				ExtensionFilter filter = new FileChooser.ExtensionFilter("BMP File (*.bmp)", "*.bmp"); //saves as a bmp to prevent any loss of data
	            fileChooser.getExtensionFilters().add(filter);
				File save = fileChooser.showSaveDialog(primaryStage);

				if (save != null) {
	                try {
	                    ImageIO.write(encodedImage, "bmp", save);
	                    status.setText("Image saved successfully.");
	                } catch (IOException ex) {
	                	//display alert notifying the user of an error
				        alert.setTitle("File Error");
				        alert.setHeaderText(null);
				        alert.setContentText("Failed to save file.");
				        alert.show();
	                }
				}
	        }
		});
		
		//decode text from image
		decodeBtn.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent e)
			{
				decodedString = Functions.decode(openedImage);
				saveTextBtn.setDisable(false); //decoded text is ready to be saved as a text file, enables the save text button
				
				//get a preview of the decoded string for the textfield
				String prev = "";
				for(int i = 0; i < 50; i++)
				{
					if(i >= decodedString.length())
						break; //exit the loop
					prev += decodedString.charAt(i);
				}
				
				decodedText.setText("Sample Decoded Text:\n" + prev); //displays the sample of the text
				status.setText("Text decoded successfully."); //updates the status
			}
			
		});

		//save decoded text as a text file
		saveTextBtn.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent e)
			{
				//saves the text file using file chooser
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Save Text File");
				ExtensionFilter filter = new FileChooser.ExtensionFilter("Text File (*.txt)", "*.txt");
	            fileChooser.getExtensionFilters().add(filter);
				File save = fileChooser.showSaveDialog(primaryStage);
				
				if (save != null) {
	                try {
	                    PrintWriter pw = new PrintWriter(save);
	                    pw.print(decodedString);
	                    pw.close();
	                    status.setText("Text file saved successfully.");
	                } catch (IOException ex) {
	                    //display alert notifying the user of an error
				        alert.setTitle("File Error");
				        alert.setHeaderText(null);
				        alert.setContentText("Error saving file.");
				        alert.show();
	                }
				}
			}
		});

	}
}
