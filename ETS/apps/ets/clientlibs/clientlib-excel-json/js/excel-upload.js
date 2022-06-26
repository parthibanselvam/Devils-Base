$(document).ready(function () {
  var selectedProduct;
       $('#xls').change(function(){
            $('#selectedFile').text($('#xls').val().replace(/C:\\fakepath\\/i, ''));
   });
    var destinationPath ;
    var fileName;
    var functionalName;
   document.getElementById('conJSON').disabled=true;

    $("#btnSubmit").click(function (event) {

        //trim the filename
        fileName = $('#xls').val().replace(/C:\\fakepath\\/i, '');
        fileName = fileName.substring(0, fileName.indexOf('.'));


        //validate input fields
        if ($("#xls").val().length > 1 && $("#productSelect").val().length > 1) {

            var filename = $("#xls").val();

            // Use a regular expression to trim everything before final dot
            var extension = filename.replace(/^.*\./, '');

            // If there is no dot anywhere in filename, we would have extension == filename
            if (extension == filename) {
                extension = '';
            } else {
                // if there is an extension, we convert to lower case
                // (N.B. this conversion will not effect the value of the extension
                // on the file upload.)
                extension = extension.toLowerCase();
            }

            if(extension != 'xls' && extension != 'xlsx')
            {
                alert("Only xls or xlsx formats are allowed!");
                event.preventDefault();
                return;
            }


            //stop submit the form, we will post it manually.
            event.preventDefault();

            // Get form
            var form = $('#fileUploadForm')[0];

            // Create an FormData object
            var data = new FormData(form);
            console.log(data);
            //data.append("destPath", $("#destPath").val());
            //destinationPath = $("#destPath").val();
            data.append("destPath", $("#productSelect").val());
            destinationPath = $("#productSelect").val();

            data.append("functionSelect", $("#functionSelect").val());
            functionalName = $("#functionSelect").val();

            // disabled the submit button
            $("#btnSubmit").prop("disabled", true);

            $(".loading").removeClass("loading--hide").addClass("loading--show");
            $(".result label").hide();

            $.ajax({
                type: "POST",
                enctype: 'multipart/form-data',
                url: "/bin/ets/json/from/xls",
                data: data,
                processData: false,
                contentType: false,
                cache: false,
                success: function (data) {

                    $(".result label").text(data);
                    $(".result label").show();
                    $(".loading").removeClass("loading--show").addClass("loading--hide");
                    document.getElementById('btnSubmit').disabled=false;
                    document.getElementById('conJSON').disabled=false;

                },
                error: function (e) {

                    $(".result label").text(e.responseText);
                    $(".result label").show();
                    $(".loading").removeClass("loading--show").addClass("loading--hide");
                    document.getElementById('btnSubmit').disabled=false;

                }
            });
        }
        else
        {
            alert("Please, fill the mandatory fields");
            // Cancel the form submission
            event.preventDefault();
            return;
        }


    });

    $('#conJSON').click(function() {
   window.location = '/mnt/overlay/dam/gui/content/commons/managepublicationwizard.html?item=' + destinationPath+'/'+functionalName+'.json';
 });


});