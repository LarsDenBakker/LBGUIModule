package nl.larsdenbakker.app.menu.registry;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import nl.larsdenbakker.property.PropertyHolder;
import nl.larsdenbakker.property.properties.Property;

public class PropertyCellValueFactory implements Callback<TableColumn.CellDataFeatures<? extends PropertyHolder, Object>, ObservableValue<Object>> {

   private final RegistriesMenu registriesMenu;
   private final Property<?> property;

   public PropertyCellValueFactory(RegistriesMenu registriesMenu, Property<?> property) {
      this.registriesMenu = registriesMenu;
      this.property = property;
   }

   @Override
   public ObservableValue<Object> call(TableColumn.CellDataFeatures<? extends PropertyHolder, Object> param) {
      PropertyHolder ph = param.getValue();
      Object propertyValue = ph.getPropertyValue(property);
      return new PropertyCellValueWrapper(registriesMenu, ph, property);
   }

}
