(function($, Granite, ns, $document) {
    "use strict";
    $(document).on("click", ".cq-dialog-submit", function (e) {
            $(window).adaptTo("foundation-ui").alert("Content modified", "The Tab container is refreshed");
        });

    }(jQuery, Granite, Granite.author, jQuery(document)));