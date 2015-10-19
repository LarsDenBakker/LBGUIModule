package nl.larsdenbakker.app.menu;

import java.util.Map;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nl.larsdenbakker.app.menu.registry.collection.CollectionPropertyMenu;
import nl.larsdenbakker.app.menu.registry.map.MapPropertyMenu;
import nl.larsdenbakker.property.PropertyHolder;
import nl.larsdenbakker.property.properties.CollectionProperty;
import nl.larsdenbakker.property.properties.MapProperty;
import nl.larsdenbakker.property.properties.Property;
import nl.larsdenbakker.registry.Registry;
import nl.larsdenbakker.util.CollectionUtils;

/**
 *
 * @author Lars den Bakker<larsdenbakker@gmail.com>
 */
public class MenuUtils {

   public static void showAlertBox(String message, String buttonText) {
      Stage window = new Stage();

      //Block events to other windows
      window.initModality(Modality.APPLICATION_MODAL);
      window.setMinWidth(250);

      Label label = new Label();
      label.setText(message);
      Button closeButton = new Button(buttonText);
      closeButton.setOnAction(e -> window.close());
      VBox layout = new VBox(10);
      layout.getChildren().addAll(label, closeButton);
      layout.setAlignment(Pos.CENTER);

      //Display window and wait for it to be closed before returning
      Scene scene = new Scene(layout);
      window.setScene(scene);
      window.showAndWait();
   }

   public static void showAlertBox(Node body, String buttonText) {
      Stage window = new Stage();

      //Block events to other windows
      window.initModality(Modality.APPLICATION_MODAL);
      window.setMinWidth(250);

      Button closeButton = new Button(buttonText);
      closeButton.setOnAction(e -> window.close());
      VBox layout = new VBox(10);
      layout.getChildren().addAll(body, closeButton);
      layout.setAlignment(Pos.CENTER);

      //Display window and wait for it to be closed before returning
      Scene scene = new Scene(layout);
      window.setScene(scene);
      window.showAndWait();
   }

   public static void showAlertBox(Node body) {
      showAlertBox(body, "Ok");
   }

   public static void showAlertBox(String message) {
      showAlertBox(message, "Ok");
   }

   public static void configureTableCell(RegistryGUIModule module, RegistryScene scene, Property<?> property, TableColumn tableColumn) {
      if (property instanceof MapProperty) {
         tableColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DummyStringConverter()));
         tableColumn.setOnEditStart(new EventHandler<TableColumn.CellEditEvent<PropertyHolder<?>, Map.Entry<Object, Object>>>() {

            @Override
            public void handle(TableColumn.CellEditEvent<PropertyHolder<?>, Map.Entry<Object, Object>> event) {
               event.consume();
               Stage stage = new Stage();
               MapPropertyMenu mapPropertyMenu = new MapPropertyMenu(module, event.getRowValue(), (MapProperty) property);
               stage.setScene(mapPropertyMenu.getScene());
               stage.setOnCloseRequest(e -> scene.refreshMenu());
               stage.initModality(Modality.APPLICATION_MODAL);
               stage.showAndWait();
            }

         });
      } else if (property instanceof CollectionProperty) {
         tableColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DummyStringConverter()));
         tableColumn.setOnEditStart(new EventHandler<TableColumn.CellEditEvent<PropertyHolder<?>, Object>>() {

            @Override
            public void handle(TableColumn.CellEditEvent<PropertyHolder<?>, Object> event) {
               event.consume();
               Stage stage = new Stage();
               CollectionPropertyMenu<?, ?> collectionPropertyMenu = new CollectionPropertyMenu<>(module, event.getRowValue(), (CollectionProperty) property);
               stage.setScene(collectionPropertyMenu.getScene());
               stage.setOnCloseRequest(e -> scene.refreshMenu());
               stage.initModality(Modality.APPLICATION_MODAL);
               stage.showAndWait();
            }

         });
      } else {
         configureTableCell(module, scene, property.getPropertyValueClass(), tableColumn);
      }
   }

   public static void configureTableCell(RegistryGUIModule module, RegistryScene scene, Class<?> cellValueType, TableColumn tableColumn) {
      //If it's an enum
      if (cellValueType.isEnum()) {
         tableColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(new RegistryStringConverter(module.getConversionModule(), cellValueType), cellValueType.getEnumConstants()));
      } else {
         Registry<?, ?> propertyValueRegistry = module.getRegistryModule().getRootRegistry().getByRegisterableClass(cellValueType);
         //If it's an object backed by a registry
         if (propertyValueRegistry != null) {
            tableColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(new RegistryStringConverter(module.getConversionModule(), cellValueType),
                                                                         CollectionUtils.asArrayOfType((Class) cellValueType, propertyValueRegistry.getAll())));
            //Otherwise use a regular text field
         } else {
            tableColumn.setCellFactory(TextFieldTableCell.forTableColumn(new RegistryStringConverter(module.getConversionModule(), cellValueType)));
         }
      }
   }

   public static Control getInputField(RegistryGUIModule module, RegistryScene scene, Class<?> cellValueType, String description) {
      //If it's an enum
      if (cellValueType.isEnum()) {
         ChoiceBox choiceBox = new ChoiceBox();
         choiceBox.setConverter(new RegistryStringConverter(module.getConversionModule(), cellValueType));
         choiceBox.getItems().addAll(cellValueType.getEnumConstants());
         return choiceBox;
      } else {
         Registry<?, ?> propertyValueRegistry = module.getRegistryModule().getRootRegistry().getByRegisterableClass(cellValueType);
         //If it's an object backed by a registry
         if (propertyValueRegistry != null) {
            ChoiceBox choiceBox = new ChoiceBox();
            choiceBox.setConverter(new RegistryStringConverter(module.getConversionModule(), cellValueType));
            choiceBox.getItems().addAll(CollectionUtils.asArrayOfType((Class) cellValueType, propertyValueRegistry.getAll()));
            return choiceBox;
            //Otherwise use a regular text field
         } else {
            TextField textField = new TextField();
            textField.setPromptText(description);
            return textField;
         }
      }
   }

   public static Object getUserInput(Control control) {
      if (control instanceof TextField) {
         return ((TextField) control).getText();
      } else if (control instanceof ChoiceBox) {
         return ((ChoiceBox) control).getValue();
      } else if (control instanceof ComboBox) {
         return ((ComboBox) control).getValue();
      } else {
         throw new IllegalArgumentException("Unable to retreive user input from control type: " + control.getClass());
      }
   }

   public static void clearUserInput(Control control) {
      if (control instanceof TextField) {
         ((TextField) control).clear();
      } else if (control instanceof ChoiceBox) {
      } else if (control instanceof ComboBox) {
      } else {
         throw new IllegalArgumentException("Unable to reset user input for control type: " + control.getClass());
      }
   }
}
