package com.spyrdonapps.weightreductor

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.list
import pl.treksoft.kvision.Application
import pl.treksoft.kvision.core.Col
import pl.treksoft.kvision.core.Color
import pl.treksoft.kvision.data.dataContainer
import pl.treksoft.kvision.form.formPanel
import pl.treksoft.kvision.form.text.Password
import pl.treksoft.kvision.form.text.Text
import pl.treksoft.kvision.html.Button
import pl.treksoft.kvision.html.Div
import pl.treksoft.kvision.html.Span
import pl.treksoft.kvision.html.button
import pl.treksoft.kvision.html.div
import pl.treksoft.kvision.html.span
import pl.treksoft.kvision.i18n.DefaultI18nManager
import pl.treksoft.kvision.i18n.I18n
import pl.treksoft.kvision.i18n.I18n.tr
import pl.treksoft.kvision.panel.FlexAlignItems
import pl.treksoft.kvision.panel.HPanel
import pl.treksoft.kvision.panel.hPanel
import pl.treksoft.kvision.panel.root
import pl.treksoft.kvision.panel.tabPanel
import pl.treksoft.kvision.panel.vPanel
import pl.treksoft.kvision.require
import pl.treksoft.kvision.rest.RestClient
import pl.treksoft.kvision.startApplication
import pl.treksoft.kvision.state.observableListOf
import kotlin.browser.window

class App : Application() {

    init {
        require("css/weightreductor.css")
    }

    override fun start() {
        I18n.manager =
            DefaultI18nManager(
                mapOf(
                    "pl" to require("i18n/messages-pl.json"),
                    "en" to require("i18n/messages-en.json")
                )
            )

        root("weightreductor") {

            val model = observableListOf(
                Data("One"),
                Data("Two"),
                Data("Three")
            )
            var debugLabel: Span? = null
            // TODO
            vPanel(spacing = 20, alignItems = FlexAlignItems.CENTER) {
                debugLabel = span("DEBUG LABEL").apply {
                    color = Color.name(Col.AQUAMARINE)
                }
                div(tr("This is a localized message."))
                val label = span("Not yet clicked.")
                var count = 0
                button("Click me").onClick {
                    label.content = "You clicked the button ${++count} times."
                    model.reverse()
                    console.log(label.content)
                }
            }
            val firstPanel = Div("First")
            val secondPanel = Div("Second")
            val thirdPanel = Div("Third")

            tabPanel {
                addTab("First", firstPanel, route = "/first")
                addTab("Second", secondPanel, route = "/second")
                addTab("Third", thirdPanel, route = "/third")
            }

            formPanel<Model> {
                add(Model::username, Text(label = "Username"), required = true)
                add(Model::password, Password(label = "Password"), required = true)
                add(Button("OK").onClick {
                    val data: Model = this@formPanel.getData()
                    println("Username: ${data.username}")
                    println("Password: ${data.password}")
                })
            }

            dataContainer(model, { data, _, _ ->
                Span(data.text)
            }, HPanel(spacing = 10))

            val restClient = RestClient()
            restClient.remoteCall(
                url = "http://localhost:8080/api/home",
                deserializer = HomeResponse.serializer()
            ) {
                it
            }.then {
                debugLabel?.content = it.toString()
            }.catch {
                console.log(it)
                window.alert(it.toString())
            }
        }
    }
}

@Serializable
data class Model(val username: String? = null, val password: String? = null)

data class Data(val text: String)

@Serializable
data class HomeResponse(val name: String)

fun main() {
    startApplication(::App)
}
