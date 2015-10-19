package nl.larsdenbakker.app.menu.registry.map;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
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
import nl.larsdenbakker.property.properties.MapProperty;
import nl.larsdenbakker.property.properties.PropertyModificationException;
import nl.larsdenbakker.util.OperationResponse;
import nl.larsdenbakker.util.TextUtils;

/**
 *
 * @author Lars den Bakker<larsdenbakker@gmail.com>
 */
public class MapPropertyMenu<K, V, M extends Map<K, V>> implements RegistryScene {

   private final RegistryGUIModule module;
   private final PropertyHolder<?> propertyHolder;
   private final MapProperty<K, V> property;
   private final Scene scene;
   private final BorderPane layout;
   private TableView<Entry<K, V>> currentTable;
   private TableColumn<Entry<K, V>, K> keyColumn;
   private TableColumn<Entry<K, V>, V> valueColumn;

   public MapPropertyMenu(RegistryGUIModule module, PropertyHolder<?> propertyHolder, MapProperty<K, V> property) {
      this.module = module;
      this.propertyHolder = propertyHolder;
      this.property = property;
      layout = new BorderPane();
      Node addButton = getMapEditTable_addButton();
      refreshMenu();
      layout.setBottom(addButton);
      scene = new Scene(layout);
   }

   public TableColumn<Entry<K, V>, K> getKeyColumn() {
      return keyColumn;
   }

   public TableColumn<Entry<K, V>, V> getValueColumn() {
      return valueColumn;
   }

   public TableView getCurrentTable() {
      return currentTable;
   }

   public PropertyHolder<?> getPropertyHolder() {
      return propertyHolder;
   }

   public MapProperty<K, V> getProperty() {
      return property;
   }

   @Override
   public Scene getScene() {
      return scene;
   }

   @Override
   public final void refreshMenu() {
      layout.setCenter(getMapEditTable());
   }

   private Region getMapEditTable() {
      currentTable = new TableView();
      currentTable.setEditable(true);
      currentTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

      keyColumn = new TableColumn(TextUtils.getDescription(property.getKeyType()));
      keyColumn.setEditable(true);
      keyColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Entry<K, V>, K>, ObservableValue<K>>() {

         @Override
         public ObservableValue<K> call(TableColumn.CellDataFeatures<Entry<K, V>, K> param) {
            return new MapPropertyCellValueWrapper(MapPropertyMenu.this, param.getValue().getKey());
         }
      });
      MenuUtils.configureTableCell(module, this, property.getKeyType(), keyColumn);

      valueColumn = new TableColumn(TextUtils.getDescription(property.getValueType()));
      valueColumn.setEditable(true);
      valueColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Entry<K, V>, V>, ObservableValue<V>>() {

         @Override
         public ObservableValue<V> call(TableColumn.CellDataFeatures<Entry<K, V>, V> param) {
            return new MapPropertyCellValueWrapper(MapPropertyMenu.this, param.getValue().getValue());
         }

      });
      MenuUtils.configureTableCell(module, this, property.getValueType(), valueColumn);

      currentTable.getItems().addAll(property.getValue(propertyHolder).entrySet());
      currentTable.getColumns().addAll(keyColumn, valueColumn);

      return currentTable;
   }

   private Node getMapEditTable_addButton() {
      Control keyControl = MenuUtils.getInputField(module, this, property.getKeyType(), TextUtils.getDescription(property.getKeyType()));
      Control valueControl = MenuUtils.getInputField(module, this, property.getValueType(), TextUtils.getDescription(property.getValueType()));

      Button addButton = new Button();
      addButton.setText("Add");
      addButton.setOnAction((ActionEvent e) -> {
         Object rawKey = MenuUtils.getUserInput(keyControl);
         Object rawValue = MenuUtils.getUserInput(valueControl);
         if (rawKey != null && rawValue != null) {
            try {
               Map copy = property.getCopy(propertyHolder);
               Object key = module.getConversionModule().convert(rawKey, property.getKeyType());
               Object value = module.getConversionModule().convert(rawValue, property.getValueType());
               MenuUtils.clearUserInput(keyControl);
               MenuUtils.clearUserInput(valueControl);
               copy.put(key, value);

               try {
                  propertyHolder.setPropertyValue(property, copy);
                  refreshMenu();
               } catch (PropertyModificationException ex) {
                  MenuUtils.showAlertBox(ex.getMessage());
               }
//                  
            } catch (ConversionException ex) {
               MenuUtils.showAlertBox(ex.getMessage());
            }
         }
      });

      Button deleteButton = new Button();
      deleteButton.setText("Delete");
      deleteButton.setOnAction((ActionEvent e) -> {
         Map<K, V> copy = property.getCopy(propertyHolder);
         ObservableList<Entry<K, V>> selectedItems = currentTable.getSelectionModel().getSelectedItems();
         for (Entry<K, V> entry : selectedItems) {
            copy.remove(entry.getKey());
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
      createBox.getChildren().addAll(keyControl, valueControl, addButton, deleteButton);
      return createBox;
   }

}
