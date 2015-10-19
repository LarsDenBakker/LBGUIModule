package nl.larsdenbakker.app.menu.registry.collection;

import java.util.Collection;
import nl.larsdenbakker.app.menu.AbstractTableCellValueWrapper;
import nl.larsdenbakker.property.properties.CollectionProperty;
import nl.larsdenbakker.property.properties.PropertyModificationException;
import nl.larsdenbakker.util.CollectionUtils;
import nl.larsdenbakker.util.OperationResponse;

/**
 *
 * @author Lars den Bakker <larsdenbakker at gmail.com>
 */
public class CollectionPropertyElementCellValueWrapper<E, C extends Collection<E>> extends AbstractTableCellValueWrapper {

   public CollectionPropertyElementCellValueWrapper(CollectionPropertyMenu<E, C> collectionPropertyMenu, Object element) {
      super(collectionPropertyMenu, element);
   }

   @Override
   public CollectionPropertyMenu<E, C> getScene() {
      return (CollectionPropertyMenu) super.getScene(); //Safe cast garaunteed by constructor
   }

   @Override
   protected void persistValue(Object value) throws PropertyModificationException {
      CollectionPropertyMenu scene = getScene();
      CollectionProperty property = scene.getProperty();
      Collection collection = CollectionUtils.instanceOf(property.getCollectionType());
      collection.addAll(scene.getCurrentTable().getItems());
      scene.getPropertyHolder().setPropertyValue(property, collection);
   }

}
