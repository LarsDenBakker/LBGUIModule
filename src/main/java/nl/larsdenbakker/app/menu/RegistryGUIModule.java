package nl.larsdenbakker.app.menu;

import java.util.List;
import javafx.stage.Stage;
import nl.larsdenbakker.app.AbstractModule;
import nl.larsdenbakker.app.Application;
import nl.larsdenbakker.app.Module;
import nl.larsdenbakker.app.menu.registry.RegistriesMenu;
import nl.larsdenbakker.conversion.ConversionModule;
import nl.larsdenbakker.property.PropertyHolderRegistry;
import nl.larsdenbakker.registry.RegistryModule;
import nl.larsdenbakker.app.ApplicationUser;
import nl.larsdenbakker.app.UserInputException;

/**
 *
 * @author Lars den Bakker <larsdenbakker at gmail.com>
 */
public class RegistryGUIModule extends AbstractModule {

   private final ApplicationUser user;
   private RegistriesMenu registriesMenu;
   private final String windowTitle;
   private final boolean scanRootRegistry;
   private final List<PropertyHolderRegistry<?, ?>> registries;

   public RegistryGUIModule(Application app, String windowTitle, Boolean scanRootRegistry, List<PropertyHolderRegistry<?, ?>> registries) {
      super(app);
      user = new ApplicationUser("admin", true, true);
      user.setToMessageForAllTypes();
      this.windowTitle = windowTitle;
      this.scanRootRegistry = scanRootRegistry;
      this.registries = registries;
   }

   @Override
   public Class<? extends Module>[] getDependencies() {
      return new Class[]{ConversionModule.class, RegistryModule.class};
   }

   public ConversionModule getConversionModule() {
      return getParentApplication().getModule(ConversionModule.class);
   }

   public RegistryModule getRegistryModule() {
      return getParentApplication().getModule(RegistryModule.class);
   }

   @Override
   protected void _load() throws UserInputException {
      registriesMenu = new RegistriesMenu(this, scanRootRegistry, registries);
   }

   @Override
   protected void _unload() {
      registriesMenu = null;
   }

   public RegistriesMenu getRegistriesMenu() {
      return registriesMenu;
   }

   public void addToStage(Stage primaryStage) {
      primaryStage.setTitle(windowTitle);
      primaryStage.setScene(registriesMenu.getScene());
      primaryStage.show();
   }

   @Override
   public String getName() {
      return "registry-gui";
   }

}
