/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
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
