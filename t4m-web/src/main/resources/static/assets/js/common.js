$(function () {
    let html = '<p class="text-justify text-white mb-3" id="has_rest_content">. . . . . .</p>';
    $('#collapse_description').before(html);
})

$('#collapse_description').on('hide.bs.collapse', function () {
    $('#has_rest_content').removeClass('d-none')
})

$('#collapse_description').on('show.bs.collapse', function () {
    $('#has_rest_content').addClass('d-none')
})