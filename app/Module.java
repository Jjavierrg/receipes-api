import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import controllers.dto.*;
import converters.*;
import converters.xml.*;
import mappers.*;
import models.entities.*;
import models.repositories.RepositoryFactory;

public class Module extends AbstractModule {
    @Override
    public void configure() {
        bind(new TypeLiteral<IXmlConverter<CategoryDto>>() {}).to(CategoryConverter.class);
        bind(new TypeLiteral<IXmlConverter<FoodDto>>() {}).to(FoodConverter.class);
        bind(new TypeLiteral<IXmlConverter<RecipeDto>>() {}).to(RecipeConverter.class);

        bind(new TypeLiteral<IMapper<Category, CategoryDto>>() {}).to(CategoryMapper.class);
        bind(new TypeLiteral<IMapper<Food, FoodDto>>() {}).to(FoodMapper.class);
        bind(new TypeLiteral<IMapper<Recipe, RecipeDto>>() {}).to(RecipeMapper.class);

        bind(ConverterFactory.class).asEagerSingleton();
        bind(RepositoryFactory.class).asEagerSingleton();
    }

}