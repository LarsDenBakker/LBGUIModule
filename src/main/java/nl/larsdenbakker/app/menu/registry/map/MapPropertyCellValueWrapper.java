package nl.larsdenbakker.app.menu.registry.map;

import java.util.Map;
import java.util.Map.Entry;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import nl.larsdenbakker.app.menu.AbstractTableCellValueWrapper;
import nl.larsdenbakker.property.properties.MapProperty;
import nl.larsdenbakker.property.properties.PropertyModificationException;
import nl.larsdenbakker.util.MapUtils;

/**
 *
 * @author Lars den Bakker <larsdenbakker at gmail.com>
 */
public class MapPropertyCellValueWrapper<K, V, M extends Map<K, V>> extends AbstractTableCellValueWrapper {

   public MapPropertyCellValueWrapper(MapPropertyMenu<K, V, M> mapPropertyMenu, Object element) {
      super(mapPropertyMenu, element);
   }

   @Override
   public MapPropertyMenu<K, V, M> getScene() {
      return (MapPropertyMenu) super.getScene(); //Safe cast garaunteed by constructor
   }

   @Override
   protected void persistValue(Object value) throws PropertyModificationException {
      MapPropertyMenu<K, V, M> scene = getScene();
      MapProperty<K, V> property = scene.getProperty();
      Map<K, V> map = MapUtils.of(property.getMapType());

      TableView<Entry<K, V>> tableView = scene.getCurrentTable();
      TableColumn<Entry<K, V>, K> keyColumn = scene.getKeyColumn();
      TableColumn<Entry<K, V>, V> valueColumn = scene.getValueColumn();

      for (int i = 0; i < tableView.getItems().size(); i++) {
         K key = keyColumn.getCellData(i);
         V val = valueColumn.getCellData(i);
         map.put(key, val);
      }

      scene.getPropertyHolder().setPropertyValue(property, map);
   }

}
