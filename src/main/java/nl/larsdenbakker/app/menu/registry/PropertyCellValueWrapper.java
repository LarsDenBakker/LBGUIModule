package nl.larsdenbakker.app.menu.registry;

import nl.larsdenbakker.app.menu.AbstractTableCellValueWrapper;
import nl.larsdenbakker.app.menu.RegistryScene;
import nl.larsdenbakker.property.PropertyHolder;
import nl.larsdenbakker.property.properties.Property;
import nl.larsdenbakker.property.properties.PropertyModificationException;

public class PropertyCellValueWrapper<V> extends AbstractTableCellValueWrapper {

   private final PropertyHolder<?> propertyHolder;
   private final Property<V> property;

   public PropertyCellValueWrapper(RegistryScene scene, PropertyHolder<?> propertyHolder, Property<V> property) {
      super(scene, propertyHolder.getPropertyValue(property));
      this.propertyHolder = propertyHolder;
      this.property = property;
   }

   public PropertyHolder getPropertyHolder() {
      return propertyHolder;
   }

   public Property getProperty() {
      return property;
   }

   @Override
   protected void persistValue(Object value) throws PropertyModificationException {
      propertyHolder.setPropertyValue(property, value);
   }

}
