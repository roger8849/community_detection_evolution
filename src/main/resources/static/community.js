
$(document).ready(function () {
  toggleFields();
  $("#loadFrom").change(function() { toggleFields(); });
});

function toggleFields(){
  if($('#loadFrom').val() === 'DATABASE'){
    $('#text').hide();
    $('#text').prop('required', null);
  } else {
    $('#text').show();
    $('#text').prop('required', true);
  }
}