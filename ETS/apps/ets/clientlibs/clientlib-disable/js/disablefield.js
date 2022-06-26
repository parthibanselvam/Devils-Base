(function($, Granite, ns, $document) {
    "use strict";

    console. log ("--------CLIENTLIBS LOADED-------");

    var PATH_SELECTOR           = ".cmp-path-url";
    var ALT_LABEL_SELECTOR         = ".cmp-alt-label";

    

// toggles the disable attribute of the Link Label and Link Title Attribute inputs, based on the Link Url existence
    function toggleDisableAttributeOnAltLabelInputs() {

         //$(ALT_LABEL_SELECTOR).prop("disabled", !$(PATH_SELECTOR).val());
        $(ALT_LABEL_SELECTOR).prop("disabled", !$(PATH_SELECTOR).val()&&!$('[name="./fileReference"]').val());
    }

    $document.on("foundation-contentloaded", function(e) {

        Coral.commons.ready($(PATH_SELECTOR, ALT_LABEL_SELECTOR), function(component) {
            toggleDisableAttributeOnAltLabelInputs();
        });
    });

    $(document).on("input", PATH_SELECTOR, function(input) {
        $(PATH_SELECTOR).val(input.target.value);
        toggleDisableAttributeOnAltLabelInputs();
    });

    $(document).on("change", PATH_SELECTOR, function(input) {
        toggleDisableAttributeOnAltLabelInputs();
    });



    }(jQuery, Granite, Granite.author, jQuery(document)));