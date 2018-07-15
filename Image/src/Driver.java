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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class Driver extends Application
{
	
	private static BufferedImage openedImage;
	private static boolean hasImage = false;
	private static boolean hasText = false;
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
        primaryStage.setTitle("Text Encoder");
		
        int xRes = 700;
        int yRes = 700;
        
        BorderPane root = new BorderPane();
        
        primaryStage.setScene(new Scene(root, xRes, yRes));
        primaryStage.show();
		
		
		Button openImgBtn = new Button("Open Image");
		Button saveImgBtn = new Button("Save Image");
		saveImgBtn.setDisable(true); //disable the save button until an encoded image is ready to be saved
		
		Button openFileBtn = new Button("Open Text File");
		Button saveFileBtn = new Button("Save Text File");
		saveFileBtn.setDisable(true);
		
		Button encodeFileBtn = new Button("Encode Text From File");
		encodeFileBtn.setDisable(true);
		Button encodeTextBtn = new Button("Encode Text From Text Box");
		encodeTextBtn.setDisable(true);
		
		Button decodeBtn = new Button("Decode Text From File");
		decodeBtn.setDisable(true);
		
		TextField decodedField = new TextField();
		decodedField.setMaxWidth(500);
		decodedField.setPrefHeight(50);
		//decodedField.setAlignment(Pos.BASELINE_LEFT);
		
		HBox openBtnBox = new HBox();
		openBtnBox.setAlignment(Pos.TOP_LEFT);
		HBox saveBtnBox = new HBox();
		saveBtnBox.setAlignment(Pos.BOTTOM_RIGHT);
		openBtnBox.getChildren().addAll(openImgBtn, openFileBtn, encodeFileBtn, decodeBtn, saveImgBtn, saveFileBtn);
		root.setBottom(openBtnBox);
		
		
		ImageView openedImageView = new ImageView();
        openedImageView.setPreserveRatio(true);
        openedImageView.setFitWidth(300);
                
    	ImageView encodedImageView = new ImageView();
        encodedImageView.setPreserveRatio(true);
        encodedImageView.setFitWidth(300);
        
		Label openedImageLabel = new Label();
		//openedImageLabel.setDisable(true);
		Label encodedImageLabel = new Label();
		//encodedImageLabel.setDisable(true);
        
        VBox openedImgVBox = new VBox();
        openedImgVBox.getChildren().addAll(openedImageView, openedImageLabel);
        VBox encodedImgVBox = new VBox();
        encodedImgVBox.getChildren().addAll(encodedImageView, encodedImageLabel);
        
        HBox imageBox = new HBox();
        imageBox.getChildren().addAll(openedImgVBox, encodedImgVBox);
        imageBox.setAlignment(Pos.BASELINE_CENTER);
		root.setTop(imageBox);
		
		root.setCenter(decodedField);
		
		Alert alert = new Alert(AlertType.INFORMATION);

        //open image
		openImgBtn.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent e)
	        {
				//System.out.println("Opening image");
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
					if(hasText)
						encodeFileBtn.setDisable(false);
					decodeBtn.setDisable(false);
						
				} catch (IOException | NullPointerException e1) {
					hasImage = false; //ensure hasIMage is still set to false
			        alert.setTitle("File Error");
			        alert.setHeaderText(null);
			        alert.setContentText("Error reading file, please open only standard image file types.");
			        alert.show();
				} catch(IllegalArgumentException e2)
				{
					//results from closing the file chooser, not a real error
					
				}
				
	        }
		 });

		//open text file
		openFileBtn.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent e)
	        {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Image");
				ExtensionFilter filter = new FileChooser.ExtensionFilter("TXT File (*.txt)", "*.txt");
	            fileChooser.getExtensionFilters().add(filter);
				
				try {
					File fileImage = fileChooser.showOpenDialog(primaryStage); 
					CharSource source = Files.asCharSource(fileImage, Charsets.UTF_8);
			    	inputString = source.read();
					
					hasText = true;
					if(hasImage)
						encodeFileBtn.setDisable(false);
						
					//System.out.println(inputString);
				} catch (IOException e1) {
					hasText = false;
					alert.setTitle("File Error");
				    alert.setHeaderText(null);
				    alert.setContentText("Error reading file, please open only text file types.");
				    alert.show();
				} catch(IllegalArgumentException | NullPointerException e2)
				{
					//results from closing the file chooser, not a real error
				}
	        }
		});
		
		//encode text from file into image
		encodeFileBtn.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent e)
	        {
				encodedImage = Functions.encode(openedImage, inputString);
				saveImgBtn.setDisable(false);
				
				Image img = SwingFXUtils.toFXImage(encodedImage, null);
				encodedImageView.setImage(img);
				encodedImageLabel.setText("Encoded Image");
	        }
		});
		
		//save encoded image
		saveImgBtn.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent e)
	        {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Save Image");
				ExtensionFilter filter = new FileChooser.ExtensionFilter("BMP File (*.bmp)", "*.bmp");
	            fileChooser.getExtensionFilters().add(filter);
				File save = fileChooser.showSaveDialog(primaryStage);

				if (save != null) {
	                try {
	                    ImageIO.write(encodedImage, "bmp", save);
	                } catch (IOException ex) {
	                    System.out.println("Failed to save file");
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
				saveFileBtn.setDisable(false);
				
				//get a preview of the decoded string for the textfield
				String prev = "";
				for(int i = 0; i < 100; i++)
				{
					if(i >= decodedString.length())
						break; //exit the loop
					prev += decodedString.charAt(i);
				}
				
				decodedField.setText(prev);
			}
			
		});

		//save decoded text as a text file
		saveFileBtn.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent e)
			{
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Save Image");
				ExtensionFilter filter = new FileChooser.ExtensionFilter("Text File (*.txt)", "*.txt");
	            fileChooser.getExtensionFilters().add(filter);
				File save = fileChooser.showSaveDialog(primaryStage);
				
				if (save != null) {
	                try {
	                    PrintWriter pw = new PrintWriter(save);
	                    pw.print(decodedString);
	                    pw.close();
	                } catch (IOException ex) {
	                    System.out.println("Failed to save file");
	                }
				}
			}
		});

	}
}
