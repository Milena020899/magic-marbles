package magicmarbles.ui

import magicmarbles.api.field.FieldBuilder
import magicmarbles.api.field.ModifiableFieldFactory
import magicmarbles.api.game.GameFactory
import magicmarbles.api.impl.Configuration
import magicmarbles.api.impl.field.FieldImpl
import magicmarbles.api.impl.field.RandomFieldBuilder
import magicmarbles.api.impl.game.GameFactoryImpl
import magicmarbles.api.impl.settings.ExtendedSettings
import magicmarbles.api.impl.settings.factory.ExtendedSettingsFactory
import magicmarbles.api.impl.settings.factory.ExtendedSettingsFactoryImpl
import magicmarbles.api.impl.settings.validator.SettingsValidator
import magicmarbles.api.impl.settings.validator.SettingsValidatorImpl
import magicmarbles.ui.configuration.ApplicationConfig
import org.kodein.di.*

fun buildDIContainer(appConfig: ApplicationConfig): DI = DI {
    bind<Configuration>() with provider { appConfig.settingsBounds.toConfig() }
    bind<ExtendedSettings>("defaultSettings") with singleton {
        instance<ExtendedSettingsFactory>()
            .build(
                appConfig.defaultSettings.width,
                appConfig.defaultSettings.height,
                appConfig.defaultSettings.minimumConnectedMarbles,
                { it * it },
                appConfig.defaultSettings.remainingMarblePenalty
            )
    }
    bind<ModifiableFieldFactory>() with provider { FieldImpl.Factory }
    bind<FieldBuilder<ExtendedSettings>>() with provider { RandomFieldBuilder(instance()) }
    bind<GameFactory<ExtendedSettings>>() with provider { GameFactoryImpl(instance(), instance()) }
    bind<SettingsValidator>() with provider { SettingsValidatorImpl(instance()) }
    bind<ExtendedSettingsFactory>() with provider { ExtendedSettingsFactoryImpl() }
    bind<GameServer>() with provider { GameServer(instance(), instance("defaultSettings")) }
}