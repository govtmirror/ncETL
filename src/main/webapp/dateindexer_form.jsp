<%-- 
    Document   : dateindexer_form
    Created on : Oct 24, 2011, 3:47:45 PM
    Author     : M. Herold
--%>

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<%
    // a little remaining jsp that's not worth removing
    List<String> stepLengths = new ArrayList();
    stepLengths.add("minute");
    stepLengths.add("hour");
    stepLengths.add("day");
    stepLengths.add("week");
    stepLengths.add("month");
    stepLengths.add("quarter");
    stepLengths.add("year");
    stepLengths.add("decade");
    stepLengths.add("century");
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Date Indexer</title>
        <!-- // TODO: localize these googleapis -->
        <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet" type="text/css"/>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js"></script>
        <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>
        <script type="text/javascript" src="js/ui/jquery-ui-timepicker-addon.js"></script>
        <script type="text/javascript" src="js/ui/date.js"></script>
        <script type="text/javascript">
        $(document).ready(function() {
            // Date-Time Picker
            $(".picker").datetimepicker({changeYear: true, changeMonth: true, 
                    dateFormat: 'yy-mm-dd', timeFormat: 'hh:mm'});
                
            // validate and ajax-submit the di_form
            $('#submit').click(function () {
                var origin = $('input[name=origin]');
                var end = $('input[name=end]');
                var stepLength = $('select[name=stepLength]');
                var leapSkip = $('input[name=leapSkip]');
                var skipInterval = $('[name=skipInterval]');
                var error = false;
                
                // origin non-empty
                if (origin.val()=='') {
                    origin.addClass("highlight");
                    error = true;
		} else 
                    origin.removeClass("highlight");
                
                // end non-empty
                if (end.val()=='') {
                    end.addClass("highlight");
                    error = true;
		} else 
                    end.removeClass("highlight");
                
                if(error)
                    return false;
                
                // origin before end
                var intervalStart = new Date(origin.val().replace(" ", "T"));
                var intervalEnd = new Date(end.val().replace(" ", "T"));
                if (intervalStart > intervalEnd) {
                    origin.addClass("highlight");
                    end.addClass("highlight");
                    return false;
		} else {
                    origin.removeClass("highlight");
                    end.removeClass("highlight");
                }
                
                // organize the data properly
		var data = 
                    'origin=' + encodeURIComponent(origin.val()) + 
                    '&end=' + encodeURIComponent(end.val()) +
                    '&stepLength=' + stepLength.val(); 
                if(leapSkip.attr("checked") == "true")
                    data += '&leapSkip=' + leapSkip.attr("checked");
                skipInterval.each(function() {
                    data += "&skipInterval=" + $(this).val();
                });
                
                // start the ajax
		$.ajax({
                    url: "dates",
                    type: "POST",
                    data: data,
                    cache: false,
                    success: function (html) {
                        $('#output').html(html);
                    }		
                });
		
		// cancel the submit button default behaviours
		return false;
            });
            
            // skipped intervals implementation
            var id = 0;
            var skipIntervals = new Array();
            var skipMsg = "<div>You don't have any skipped intervals.</div>";
            $('#skipIntervals').html(skipMsg);
            
            $('#add').click(function () {
                var skipIntervalStart = $('input[name=skipIntervalStart]');
                var skipIntervalEnd = $('input[name=skipIntervalEnd]');
                var error = false;
                
                // start non-empty
                if (skipIntervalStart.val()=='') {
                    skipIntervalStart.addClass("highlight");
                    error = true;
		} else 
                    skipIntervalStart.removeClass("highlight");
                
                // end non-empty
                if (skipIntervalEnd.val()=='') {
                    skipIntervalEnd.addClass("highlight");
                    error = true;
		} else 
                    skipIntervalEnd.removeClass("highlight");
                
                if(error)
                    return false;
                
                // start before end
                var intervalStart = new Date(skipIntervalStart.val().replace(" ", "T"));
                var intervalEnd = new Date(skipIntervalEnd.val().replace(" ", "T"));

                if (intervalStart.getTime() > intervalEnd.getTime()) {
                    skipIntervalStart.addClass("highlight");
                    skipIntervalEnd.addClass("highlight");
                    return false;
		} else {
                    skipIntervalStart.removeClass("highlight");
                    skipIntervalEnd.removeClass("highlight");
                }
                
                var skipInterval = skipIntervalStart.val() + "/" + skipIntervalEnd.val();
                
                // restrict duplicates
                if($.inArray(skipInterval, skipIntervals) > -1)
                    return false;

                // add to the array for size-checking
                skipIntervals.push(skipInterval);

                // remove the empty message
                if($('#skipIntervals').html() == skipMsg)
                    $('#skipIntervals').empty();
                
                // create the label, the remove button and the hidden field
                $('#skipIntervals').append('<div id="si_' + id + '"><label for="rsi_' + id + '">' + skipInterval + '</label>'
                                        + '<input type="button" value="Remove" name="remove" id="rsi_'+ id + '" /></div>');   
                var hiddenInput = '<input type="hidden" name="skipInterval" id="hsi_' + id 
                                        + '" value="' + skipInterval + '" />';
                $('#hiddenInput').append(hiddenInput);
                
                // tie a remove event to the newly created remove button       
                $('[id^="rsi_' + id + '"]').click(function() {
                    var rsi_id = $(this).attr("id");
                    var array = rsi_id.split("_");
                    var id = array[1];
                    var skipInterval = $('#hsi_'+id).val();
                    var index = skipIntervals.indexOf(skipInterval);
                    skipIntervals.splice(index, 1);
                    if(skipIntervals.length < 1)
                        $('#skipIntervals').html(skipMsg);
                    $('#hsi_'+id).remove();
                    $('#si_'+id).remove();
                    return false;
                });
                
                id = id + 1;
                
                return false;
            });
            
            $('#clear').click(function() {
                skipIntervals = new Array();
                $('#skipIntervals').html(skipMsg);
                $('#hiddenInput').empty();
                return false;
            });
            
            // clear error boxes on focus
            $("input").focusin(function() {
                $(this).removeClass("highlight");
            });
            
        });
        </script>
        <style>
            .picker {width: 115px;} 
            .ui-datepicker {font-size: 75%;} 
            #skipIntervals {color: grey;} 
            .highlight {border-color: red; border-style: solid; border-width: 2px;}
        </style>
    </head>
    <body>
        <h1>Date Indexer</h1>
        
        <form name="di_form">
            Interval: <input type="text" name="origin" class="picker" /> to
            <input type="text" name="end" class="picker" /><br />
            Step length: 
            <select name="stepLength">
                <% 
                for(String s : stepLengths) { 
                    if(s.equals("day")) { %>
                        <option value="<%=s%>" selected="true"><%=s%></option> <%
                    } else { %>
                        <option value="<%=s%>"><%=s%></option>
                <%  } 
                } %>
            </select><br />
            <label for="leapSkip">Skip leap days </label><input type="checkbox" name="leapSkip" id="leapSkip" /><br />
            <div id="hiddenInput"></div>
            
            <hr />
            Add an interval to skip: <input type="text" name="skipIntervalStart" class="picker" /> to  
            <input type="text" name="skipIntervalEnd" class="picker" />
            <input type="button" value="Add" name="add" id="add" />
            <input type="button" value="Clear" name="clear" id="clear" />
            
            <table id="skipIntervals"></table>
            <hr />
            <input type="submit" value="Submit" id="submit" />
        </form>
        <br />
        <div id="output"></div>
    </body>
</html>
