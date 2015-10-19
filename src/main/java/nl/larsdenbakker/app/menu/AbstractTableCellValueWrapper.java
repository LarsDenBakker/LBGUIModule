package nl.larsdenbakker.app.menu;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import nl.larsdenbakker.property.properties.PropertyModificationException;

public abstract class AbstractTableCellValueWrapper implements javafx.beans.property.Property<Object>, ObservableValue<Object> {

   private final RegistryScene scene;
   private Object value;

   public AbstractTableCellValueWrapper(RegistryScene scene, Object value) {
      this.scene = scene;
      this.value = value;
   }

   List<ChangeListener<? super String>> changeListeners;
   List<InvalidationListener> invalidationListeners;

   @Override
   public void addListener(ChangeListener<? super Object> listener) {
      if (changeListeners == null) {
         changeListeners = new ArrayList();
      }
      if (!changeListeners.contains(listener)) {
         changeListeners.add(listener);
      }
   }

   @Override
   public void removeListener(ChangeListener<? super Object> listener) {
      if (changeListeners != null) {
         changeListeners.remove(listener);
      }
   }

   @Override
   public void addListener(InvalidationListener listener) {
      if (invalidationListeners == null) {
         invalidationListeners = new ArrayList();
      }
      if (!invalidationListeners.contains(listener)) {
         invalidationListeners.add(listener);
      }
   }

   @Override
   public void removeListener(InvalidationListener listener) {
      if (invalidationListeners != null) {
         invalidationListeners.remove(listener);
      }
   }

   @Override
   public void setValue(Object value) {
      if (value != null) {
         try {
            persistValue(value);
            this.value = value;

         } catch (PropertyModificationException ex) {
            MenuUtils.showAlertBox(ex.getMessage());
            scene.refreshMenu();
         }
      } else {
         scene.refreshMenu();//If null it means it was not converted in the RegistryStringConverter
      }
   }

   public RegistryScene getScene() {
      return scene;
   }

   protected abstract void persistValue(Object value) throws PropertyModificationException;

   @Override
   public Object getValue() {
      return value;
   }

   @Override
   public String getBean() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public String getName() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void bind(ObservableValue<? extends Object> observable) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void unbind() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public boolean isBound() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void bindBidirectional(javafx.beans.property.Property<Object> other) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void unbindBidirectional(javafx.beans.property.Property<Object> other) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

}
