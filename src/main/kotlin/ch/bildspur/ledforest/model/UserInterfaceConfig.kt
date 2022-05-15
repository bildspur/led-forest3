package ch.bildspur.ledforest.model

import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.NumberParameter
import com.google.gson.annotations.Expose

class UserInterfaceConfig {
    @Expose
    @NumberParameter("Selected Preview Tab")
    var selectedPreviewTab = DataModel(1)
}
