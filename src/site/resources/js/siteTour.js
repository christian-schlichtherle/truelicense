/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

$(function () {
    $('#startSiteTour').on('click', function () {
        var dropDownMenus = $(".dropdown-toggle");
        return hopscotch.startTour({
            id: "site",
            steps: [
                {
                    arrowOffset: 1,
                    target: dropDownMenus[0],
                    placement: "left",
                    title: "Modules",
                    content: "TrueLicense is organized into multiple modules and each one has it's own microsite. The module TrueLicense Maven Archetype provides a code generator for custom licensing schemas and is a good starting point."
                },
                {
                    arrowOffset: 1,
                    target: dropDownMenus[1],
                    placement: "left",
                    title: "Documentation",
                    content: "This menu provides the specific documentation for the current module. The top level module provides general information about TrueLicense."
                },
                {
                    arrowOffset: 1,
                    target: dropDownMenus[2],
                    placement: "left",
                    title: "Reports",
                    content: "This is where Maven puts its automated reports for the current module. The top level module provides Javadocs and Source Xref for the entire project."
                },
                {
                    arrowOffset: 1,
                    target: dropDownMenus[3],
                    placement: "left",
                    title: "External Links",
                    content: "This is a set of links to external resources, e.g. the project's road map and change log. External links are not specific to a module."
                },
                {
                    arrowOffset: 1,
                    target: "breadcrumbs",
                    placement: "left",
                    title: "Breadcrumbs",
                    content: "The breadcrumbs show you were you are and provide quick links to the parent project(s)."
                }
            ]
        });
    });
});
