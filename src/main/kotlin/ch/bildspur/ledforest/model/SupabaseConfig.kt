package ch.bildspur.ledforest.model

import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.StringParameter
import com.google.gson.annotations.Expose

class SupabaseConfig {
    @Expose
    @BooleanParameter("Enabled", useToggleSwitch = true)
    var enabled = DataModel(false)

    @Expose
    @StringParameter("Project Url")
    var projectUrl = DataModel("")

    @Expose
    @StringParameter("Project Secret")
    var projectSecret = DataModel("")

    @Expose
    @StringParameter("Installation Key")
    var installationKey = DataModel("")

    @Expose
    @StringParameter("User Email")
    var userEmail = DataModel("")

    @Expose
    @StringParameter("User Password")
    var userPassword = DataModel("")

    @StringParameter("Installation Name", isEditable = false)
    var installationName = DataModel("")
}