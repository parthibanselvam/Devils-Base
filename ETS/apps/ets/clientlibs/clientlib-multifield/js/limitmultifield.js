(function($, Coral) {
    "use strict" ;

    console. log ("--------CLIENTLIBS LOADED-------");

    var registry = $(window).adaptTo("foundation-registry");

    // Validator for required for multifietd max and min items
    registry. register("foundation.validation.validator", {
        selector: "[data-validation=ets-multifield]" ,
        validate: function(element) {
            var el = $(element);
            let max = el.data("max-items");
            let min = el.data("min-items");
            let items = el.children("coral-multifield-item").length;
            let domitems = el.children("coral-multifield-item");
            console.log ( "{} : {} : {} ",max,min,items);
            if(items > max){
            domitems.last().remove();
            return "You can maximum add only "+max+" cards. You are trying to add "+items+"th card."
            }
            if(items < min){
            return "You must add atleast "+min+" card."
            }
        }
    });

})(jQuery, Coral);