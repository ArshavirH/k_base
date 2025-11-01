package com.buildware.kbase.project.web.dto;

/**
 * Visibility of a project as exposed by the API. PUBLIC content is accessible broadly; CONFIDENTIAL content requires
 * additional authorization and is excluded from default listings.
 */
public enum VisibilityDTO {

    /**
     * Publicly visible project.
     */
    PUBLIC,

    /**
     * Confidential project, hidden by default.
     */
    CONFIDENTIAL
}
