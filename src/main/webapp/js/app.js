function logout() {
    //clear existing  msgs
    $('span.invalid').remove();
    $('span.success').remove();

    $.ajax({
        url: 'rest/refimpl',
        type: "DELETE",
        success: function(data) {
            window.location.reload(true);
        },
        error: function(error) {
            if ((error.status == 409) || (error.status == 400)) {
                //console.log("Validation error registering user!");

                var errorMsg = $.parseJSON(error.responseText);

                $.each(errorMsg, function(index, val) {
                    $('<span class="invalid">' + val + '</span>').insertAfter($('#' + index));
                });
            } else {
                //console.log("error - unknown server issue");
                $('#formMsgs').append($('<span class="invalid">Unknown server error</span>'));
            }
        }
    });
}

function deleteSubscription(myData) {
    //clear existing  msgs
    $('span.invalid').remove();
    $('span.success').remove();

    $.ajax({
        url: 'rest/refimpl',
        type: "PUT",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(myData),
        success: function(data) {
            window.location = "home.jsf";
        },
        error: function(error) {
            if ((error.status == 409) || (error.status == 400)) {
                //console.log("Validation error registering user!");

                var errorMsg = $.parseJSON(error.responseText);

                $.each(errorMsg, function(index, val) {
                    $('<span class="invalid">' + val + '</span>').insertAfter($('#' + index));
                });
            } else {
                //console.log("error - unknown server issue");
                $('#formMsgs').append($('<span class="invalid">Unknown server error</span>'));
            }
        }
    });
}


function home() {
    window.location = "home.jsf";
}
