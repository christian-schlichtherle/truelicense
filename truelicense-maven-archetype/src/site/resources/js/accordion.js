/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

$(function () {
    $('.accordion').each(function () {
        $(this)
            .find('.accordion-toggle')
            .attr('data-parent', '#' + this.id)
            .attr('data-toggle', 'collapse');
    });
});
