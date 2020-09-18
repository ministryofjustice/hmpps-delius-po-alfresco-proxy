package uk.gov.gsi.justice.alfresco.proxy.service;

public interface DependencyHealthChecker<T> {
    T checkDependencyHealth();
}