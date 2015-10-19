package nl.larsdenbakker.app.menu;

import javafx.util.StringConverter;
import nl.larsdenbakker.util.TextUtils;

/**
 *
 * @author Lars den Bakker <larsdenbakker at gmail.com>
 */
public class DummyStringConverter<T> extends StringConverter<T> {

   @Override
   public String toString(T object) {
      return TextUtils.getDescription(object);
   }

   @Override
   public T fromString(String string) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

}
