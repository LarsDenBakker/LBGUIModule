package nl.larsdenbakker.app.menu.registry;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import nl.larsdenbakker.app.menu.MenuUtils;
import nl.larsdenbakker.app.menu.RegistryGUIModule;
import nl.larsdenbakker.app.menu.RegistryScene;
import nl.larsdenbakker.property.PropertyHolder;
import nl.larsdenbakker.property.PropertyHolderCreationException;
import nl.larsdenbakker.property.PropertyHolderRegistry;
import nl.larsdenbakker.property.properties.Property;
import nl.larsdenbakker.registry.Registry;

/**
 *
 * @author Lars den Bakker<larsdenbakker@gmail.com>
 */
public class RegistriesMenu implements RegistryScene {

   private final RegistryGUIModule module;
   private final Scene scene;
   private final BorderPane layout;
   private final List<PropertyHolderRegistry<?, ?>> registries;
   private final boolean scanRootRegistry;

   private PropertyHolderRegistry currentRegistry;
   private TableView currentTable;

   public RegistriesMenu(RegistryGUIModule module, boolean scanRootRegistry, List<PropertyHolderRegistry<?, ?>> registries) {
      this.scanRootRegistry = scanRootRegistry;
      this.registries = registries;
      this.module = module;
      layout = new BorderPane();
      Node navigation = getRegistryNavigation();
      layout.setTop(navigation);
      BorderPane.setMargin(navigation, new Insets(12, 12, 12, 12));
      refreshMenu();
      scene = new Scene(layout, 1000, 1000);
   }

   public RegistriesMenu(RegistryGUIModule module) {
      this(module, true, null);
   }

   public RegistriesMenu(RegistryGUIModule module, boolean scanRootRegistry) {
      this(module, scanRootRegistry, null);
   }

   @Override
   public final void refreshMenu() {
      Node body = getBodyRegion(currentRegistry);
      layout.setCenter(body);
   }

   @Override
   public Scene getScene() {
      return scene;
   }

   private Region getRegistryNavigation() {
      AnchorPane pane = new AnchorPane();
      pane.setPrefHeight(60);
      Text title = new Text("Registries");
      title.setFont(new Font("Arial", 18));
      HBox titleBox = new HBox();
      titleBox.getChildren().add(title);
      titleBox.setPadding(new Insets(0, 0, 10, 0));
      titleBox.setSpacing(50.0);
      HBox navigationBox = new HBox();
      ObservableList<Node> children = navigationBox.getChildren();

      if (registries != null) {
         for (Registry<?, ?> registry : registries) {
            if (registry instanceof PropertyHolderRegistry) {
               if (currentRegistry == null) {
                  this.currentRegistry = ((PropertyHolderRegistry<?, ?>) registry);
               }
               children.add(createRegistryNavigationButton(((PropertyHolderRegistry<?, ?>) registry)));
            }
         }
      }
      if (scanRootRegistry) {
         for (Registry<?, ?> registry : module.getRegistryModule().getRootRegistry().getAll()) {
            if (registry instanceof PropertyHolderRegistry) {
               if (currentRegistry == null) {
                  this.currentRegistry = ((PropertyHolderRegistry<?, ?>) registry);
               }
               children.add(createRegistryNavigationButton(((PropertyHolderRegistry<?, ?>) registry)));
            }
         }
      }

      pane.getChildren().add(titleBox);
      pane.getChildren().add(navigationBox);
      AnchorPane.setTopAnchor(titleBox, 0.0);
      AnchorPane.setBottomAnchor(navigationBox, 0.0);

      return pane;
   }

   private Button createRegistryNavigationButton(PropertyHolderRegistry<?, ?> registry) {
      Button button = new Button();
      button.setText(registry.getPluralDataValueDescription());
      button.setOnAction((ActionEvent e) -> {
         currentRegistry = registry;
         refreshMenu();
      });
      return button;
   }

   private Region getBodyRegion(PropertyHolderRegistry<?, ?> registry) {
      Node title = getBodyRegion_title(registry);
      currentTable = getBodyRegion_table(registry);
      Node createBox = getBodyRegion_createBox(registry);

      VBox vbox = new VBox();
      vbox.setSpacing(5);
      vbox.setPadding(new Insets(10, 10, 0, 10));
      vbox.getChildren().addAll(title, currentTable, createBox);
      return vbox;
   }

   private Node getBodyRegion_title(PropertyHolderRegistry<?, ?> registry) {
      Label label = new Label(registry.getPluralDataValueDescription());
      label.setFont(new Font("Arial", 18));
      return label;
   }

   private TableView getBodyRegion_table(PropertyHolderRegistry<?, ?> registry) {
      TableView<PropertyHolder> table = new TableView();

      Property<?>[] properties = registry.getProperties().getAll();

      for (Property<?> p : properties) {
         TableColumn tableColumn = new TableColumn(p.getDescription());
         tableColumn.setEditable(true);
         tableColumn.setCellValueFactory(new PropertyCellValueFactory(this, p));
         MenuUtils.configureTableCell(module, this, p, tableColumn);
         table.getColumns().add(tableColumn);
      }

      table.setEditable(true);
      table.getItems().addAll(registry.getAll());
      table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
      return table;
   }

   private Node getBodyRegion_createBox(PropertyHolderRegistry<?, ?> registry) {
      List<Property<?>> constructorProperties = registry.getProperties().getConstructorParameterProperties();
      List<Control> constructorFields = new ArrayList();
      for (Property<?> p : constructorProperties) {
         constructorFields.add(MenuUtils.getInputField(module, this, p.getPropertyValueClass(), p.getDescription()));
      }

      Button createButton = new Button();
      createButton.setText("Create");
      createButton.setOnAction((ActionEvent e) -> {
         ArrayList<Object> arguments = new ArrayList();
         for (Control control : constructorFields) {
            Object userInput = MenuUtils.getUserInput(control);
            if (userInput != null && (userInput instanceof String && !((String) userInput).isEmpty())) {
               arguments.add(userInput);
            } else {
               return;
            }
         }

         try {
            PropertyHolder propertyHolder = registry.createAndRegister(arguments.toArray());
            MenuUtils.showAlertBox("You have created " + propertyHolder.getTypeAndValueDescription());
            refreshMenu();
         } catch (PropertyHolderCreationException ex) {
            MenuUtils.showAlertBox(ex.getMessage());
         }

      });

      HBox createBox = new HBox();
      createBox.setSpacing(3);
      createBox.getChildren().addAll(constructorFields);
      createBox.getChildren().add(createButton);
      createBox.getChildren().add(getBodyRegion_deleteButton(registry));
      return createBox;
   }

   private Node getBodyRegion_deleteButton(PropertyHolderRegistry<?, ?> registry) {
      Button button = new Button();
      button.setText("Delete");
      button.setOnAction(e -> {
         currentRegistry.unregisterAll(currentTable.getSelectionModel().getSelectedItems());
         refreshMenu();
      });
      return button;
   }

}
