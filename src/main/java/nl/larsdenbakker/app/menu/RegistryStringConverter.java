package nl.larsdenbakker.app.menu;

import javafx.util.StringConverter;
import nl.larsdenbakker.conversion.ConversionException;
import nl.larsdenbakker.conversion.ConversionModule;
import nl.larsdenbakker.util.TextUtils;

/**
 *
 * @author Lars den Bakker<larsdenbakker@gmail.com>
 */
public class RegistryStringConverter<T> extends StringConverter<T> {

   private final Class<T> type;
   private final ConversionModule conversionModule;

   public RegistryStringConverter(ConversionModule conversionModule, Class<T> type) {
      this.type = type;
      this.conversionModule = conversionModule;
   }

   @Override
   public String toString(T object) {
      return TextUtils.getDescription(object, "");
   }

   @Override
   public T fromString(String string) {
      try {
         return conversionModule.convert(string, type);
      } catch (ConversionException ex) {
         MenuUtils.showAlertBox(ex.getMessage());
         return null;
      }
   }

}
