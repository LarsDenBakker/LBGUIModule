package nl.larsdenbakker.app.menu.registry.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import nl.larsdenbakker.app.menu.MenuUtils;
import nl.larsdenbakker.app.menu.RegistryGUIModule;
import nl.larsdenbakker.app.menu.RegistryScene;
import nl.larsdenbakker.conversion.ConversionException;
import nl.larsdenbakker.property.PropertyHolder;
import nl.larsdenbakker.property.properties.CollectionProperty;
import nl.larsdenbakker.property.properties.PropertyModificationException;
import nl.larsdenbakker.util.OperationResponse;
import nl.larsdenbakker.util.TextUtils;

/**
 *
 * @author Lars den Bakker<larsdenbakker@gmail.com>
 */
public class CollectionPropertyMenu<E, C extends Collection<E>> implements RegistryScene {

   private final RegistryGUIModule module;
   private final PropertyHolder<?> propertyHolder;
   private final CollectionProperty<E, C> property;
   private final Scene scene;
   private final BorderPane layout;
   private TableView<E> currentTable;

   public CollectionPropertyMenu(RegistryGUIModule module, PropertyHolder<?> propertyHolder, CollectionProperty<E, C> property) {
      this.module = module;
      this.propertyHolder = propertyHolder;
      this.property = property;
      layout = new BorderPane();
      Node addButton = getCollectionEditTable_addButton();
      refreshMenu();
      layout.setBottom(addButton);
      scene = new Scene(layout);
   }

   public TableView<E> getCurrentTable() {
      return currentTable;
   }

   public PropertyHolder<?> getPropertyHolder() {
      return propertyHolder;
   }

   public CollectionProperty<E, C> getProperty() {
      return property;
   }

   public Scene getScene() {
      return scene;
   }

   public void refreshMenu() {
      layout.setCenter(getCollectionEditTable());
   }

   private Region getCollectionEditTable() {
      currentTable = new TableView();
      currentTable.setEditable(true);
//      return new CollectionPropertyElementCellValueWrapper(collectionPropertyMenu, param.getValue());

      TableColumn column = new TableColumn(TextUtils.getDescription(property.getCollectionElementType()));
      column.setEditable(true);
      column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Collection<E>, E>, ObservableValue<E>>() {

         @Override
         public ObservableValue<E> call(TableColumn.CellDataFeatures<Collection<E>, E> param) {
            return new CollectionPropertyElementCellValueWrapper(CollectionPropertyMenu.this, param.getValue());
         }
      });
      MenuUtils.configureTableCell(module, this, property.getCollectionElementType(), column);

      currentTable.getItems().addAll(property.getValue(propertyHolder));
      currentTable.getColumns().add(column);
      currentTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
      return currentTable;
   }

   private Node getCollectionEditTable_addButton() {
      Control inputControl = MenuUtils.getInputField(module, this, property.getCollectionElementType(), property.getDescription());

      Button addButton = new Button();
      addButton.setText("Add");
      addButton.setOnAction((ActionEvent e) -> {
         Object rawElement = MenuUtils.getUserInput(inputControl);
         if (rawElement != null) {
            try {
               C copy = property.getCopy(propertyHolder);
               E key = module.getConversionModule().convert(rawElement, property.getCollectionElementType());
               MenuUtils.clearUserInput(inputControl);
               copy.add(key);
               try {
                  propertyHolder.setPropertyValue(property, copy);
                  refreshMenu();
               } catch (PropertyModificationException ex) {
                  MenuUtils.showAlertBox(ex.getMessage());
               }
            } catch (ConversionException ex) {
               MenuUtils.showAlertBox(ex.getMessage());
            }
         }
      });

      Button deleteButton = new Button();
      deleteButton.setText("Delete");
      deleteButton.setOnAction((ActionEvent e) -> {
         C copy = property.getCopy(propertyHolder);
         if (ArrayList.class.isAssignableFrom(property.getCollectionType())) {
            //sorted from small to large to safely remove from arraylist from large to small
            SortedList<Integer> selectedIndices = currentTable.getSelectionModel().getSelectedIndices().sorted();
            ArrayList<E> arrayList = (ArrayList) copy;
            for (int i = selectedIndices.size() - 1; i >= 0; i--) {
               arrayList.remove(selectedIndices.get(i).intValue());
            }
         } else {
            ObservableList<E> selectedItems = currentTable.getSelectionModel().getSelectedItems();
            copy.removeAll(selectedItems);
         }
         try {
            propertyHolder.setPropertyValue(property, copy);
            refreshMenu();
         } catch (PropertyModificationException ex) {
            MenuUtils.showAlertBox(ex.getMessage());
         }
      });
      HBox createBox = new HBox();
      createBox.setSpacing(3);
      createBox.getChildren().addAll(inputControl, addButton, deleteButton);

      return createBox;
   }

}
