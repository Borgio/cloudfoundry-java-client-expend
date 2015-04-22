/*
 * Copyright 2009-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.client.lib.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.cloudfoundry.client.lib.domain.CloudAdminBuildpack;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudDomain;
import org.cloudfoundry.client.lib.domain.CloudEntity;
import org.cloudfoundry.client.lib.domain.CloudEvent;
import org.cloudfoundry.client.lib.domain.CloudOrganization;
import org.cloudfoundry.client.lib.domain.CloudQuota;
import org.cloudfoundry.client.lib.domain.CloudRoute;
import org.cloudfoundry.client.lib.domain.CloudSecurityGroup;
import org.cloudfoundry.client.lib.domain.CloudSecurityRules;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudServiceBinding;
import org.cloudfoundry.client.lib.domain.CloudServiceBroker;
import org.cloudfoundry.client.lib.domain.CloudServiceInstance;
import org.cloudfoundry.client.lib.domain.CloudServiceOffering;
import org.cloudfoundry.client.lib.domain.CloudServicePlan;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.CloudSpaceQuota;
import org.cloudfoundry.client.lib.domain.CloudStack;
import org.cloudfoundry.client.lib.domain.CloudUser;
import org.cloudfoundry.client.lib.domain.CloudUserNoUaa;
import org.cloudfoundry.client.lib.domain.Staging;

/**
 * Class handling the mapping of the cloud domain objects
 *
 * @author Thomas Risberg
 */
//TODO: use some more advanced JSON mapping framework?
public class CloudEntityResourceMapper {

	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	public String getNameOfResource(Map<String, Object> resource) {
		return getEntityAttribute(resource, "name", String.class);
	}

	public UUID getGuidOfResource(Map<String, Object> resource) {
		return getMeta(resource).getGuid();
	}

	@SuppressWarnings("unchecked")
	public <T> T mapResource(Map<String, Object> resource, Class<T> targetClass) {
		if (targetClass == CloudSpace.class) {
			return (T) mapSpaceResource(resource);
		}
		if (targetClass == CloudOrganization.class) {
			return (T) mapOrganizationResource(resource);
		}
		if (targetClass == CloudDomain.class) {
			return (T) mapDomainResource(resource);
		}
		if (targetClass == CloudRoute.class) {
			return (T) mapRouteResource(resource);
		}
		if (targetClass == CloudApplication.class) {
			return (T) mapApplicationResource(resource);
		}
		if (targetClass == CloudService.class) {
			return (T) mapServiceResource(resource);
		}
		if (targetClass == CloudServiceInstance.class) {
			return (T) mapServiceInstanceResource(resource);
		}
		if (targetClass == CloudServiceOffering.class) {
			return (T) mapServiceOfferingResource(resource);
		}
		if (targetClass == CloudServiceBroker.class) {
			return (T) mapServiceBrokerResource(resource);
		}
		if (targetClass == CloudStack.class) {
			return (T) mapStackResource(resource);
		}
		if (targetClass == CloudQuota.class) {
            return (T) mapQuotaResource(resource);
        }
		if (targetClass == CloudUser.class) {
			return (T) mapUserResource(resource);
		}
		if (targetClass == CloudUserNoUaa.class) {
			return (T) mapUserNoUaaResource(resource);
		}
		if (targetClass == CloudSpaceQuota.class) {
			return (T) mapSpaceQuotaResource(resource);
		}
		if (targetClass == CloudSecurityGroup.class) {
			return (T) mapSecurityGroupResource(resource);
		}
		if (targetClass == CloudEvent.class) {
			return (T) mapCloudEventResource(resource);
		}
		if (targetClass == CloudAdminBuildpack.class) {
			return (T) mapCloudAdminBuildpackResource(resource);
		}
		throw new IllegalArgumentException(
				"Error during mapping - unsupported class for entity mapping " + targetClass.getName());
	}

	private CloudAdminBuildpack mapCloudAdminBuildpackResource(Map<String, Object> resource) {
		int position= (int) getEntity(resource).get("position");
		Boolean enabled = (Boolean) getEntity(resource).get("enabled");
		Boolean locked = (Boolean) getEntity(resource).get("locked");
		String filename = (String) getEntity(resource).get("filename");
		return new CloudAdminBuildpack(getMeta(resource), getNameOfResource(resource), filename, position, enabled, locked);
	}

	@SuppressWarnings("unchecked")
	private CloudEvent mapCloudEventResource(Map<String, Object> resource) {
		String type = (String) getEntity(resource).get("type");
		String actor = (String) getEntity(resource).get("actor");
		String actor_type = (String) getEntity(resource).get("actor_type");
		String actor_name = (String) getEntity(resource).get("actor_name");
		String actee = (String) getEntity(resource).get("actee");
		String actee_type = (String) getEntity(resource).get("actee_type");
		String actee_name = (String) getEntity(resource).get("actee_name");
		String timestamp = (String) getEntity(resource).get("timestamp");
		String space_guid = (String) getEntity(resource).get("space_guid");
		String organization_guid = (String) getEntity(resource).get("organization_guid");
		LinkedHashMap<String, Object> metadataMap = (LinkedHashMap<String, Object>) getEntity(resource).get("metadata");
		String description = "";
		if (metadataMap.containsKey("request")) {
			description = ((LinkedHashMap<String, Object>)metadataMap.get("request")).toString().replaceAll("\\{\\}", "");
		} else {
			description = metadataMap.toString().replaceAll("\\{\\}", "");
		}		
		return new CloudEvent(getMeta(resource), getNameOfResource(resource), type, actor, actor_type, actor_name,
				actee, actee_type, actee_name, timestamp, space_guid, organization_guid, description);
	}

	@SuppressWarnings("unchecked")
	private CloudSecurityGroup mapSecurityGroupResource(Map<String, Object> resource) {
		
		Boolean running_default = (Boolean) getEntity(resource).get("running_default");
		Boolean staging_default = (Boolean) getEntity(resource).get("staging_default");
		List<CloudSpace> cloudSpaces = new ArrayList<CloudSpace>();
		if (getEntity(resource).get("spaces") != null){
			cloudSpaces = getSpaceListByRole("spaces", resource);
		} else {
			cloudSpaces = null;
		} 
				
		List<Map<String, Object>> rules = (List<Map<String, Object>>) getEntity(resource).get("rules");
		List<CloudSecurityRules> cloudSecurityRules = new ArrayList<CloudSecurityRules>();
		if (rules.size() != 0) {
			for (Map<String, Object> rule : rules) {
				if (rule.get("protocol").equals("icmp")) {
					String protocol = (String)rule.get("protocol");
					String destination = (String)rule.get("destination");
					int type = (int) rule.get("type");
					int code= (int) rule.get("code");
					CloudSecurityRules securityRule = new CloudSecurityRules(protocol, destination, type, code);
					cloudSecurityRules.add(securityRule);
				}				
				if (rule.get("protocol").equals("tcp")) {
					String protocol = (String)rule.get("protocol");
					String destination = (String)rule.get("destination");
					String ports = (String)rule.get("ports");
					Boolean log = (Boolean) rule.get("log");
					CloudSecurityRules securityRule = new CloudSecurityRules(protocol, destination, ports, log);
					cloudSecurityRules.add(securityRule);
				}
				if (rule.get("protocol").equals("udp")) {
					String protocol = (String)rule.get("protocol");
					String destination = (String)rule.get("destination");
					String ports = (String)rule.get("ports");
					CloudSecurityRules securityRule = new CloudSecurityRules(protocol, destination, ports);
					cloudSecurityRules.add(securityRule);
				}
				if (rule.get("protocol").equals("all")) {
					String protocol = (String)rule.get("protocol");
					String destination = (String)rule.get("destination");
					CloudSecurityRules securityRule = new CloudSecurityRules(protocol, destination);
					cloudSecurityRules.add(securityRule);
				}
			}
		}		
		return new CloudSecurityGroup(getMeta(resource), getNameOfResource(resource), 
				cloudSecurityRules, running_default, staging_default, cloudSpaces);
	}

	private CloudUser mapUserResource(Map<String, Object> resource) {
		
		
		Boolean isAdmin = (Boolean) getEntity(resource).get("admin");
		Boolean isActive = (Boolean) getEntity(resource).get("active");
		
		if (getEntity(resource).get("username") != null) {
			
			String username = (String) getEntity(resource).get("username");
			List<CloudOrganization> organizations = getOrgListByRole("organizations", resource);
			List<CloudOrganization> managed_organizations = getOrgListByRole("managed_organizations", resource);
			List<CloudOrganization> audited_organizations = getOrgListByRole("audited_organizations", resource);
			
			List<CloudSpace> spaces = getSpaceListByRole("spaces", resource);
			List<CloudSpace> managed_spaces = getSpaceListByRole("managed_spaces", resource);
			List<CloudSpace> audited_spaces = getSpaceListByRole("audited_spaces", resource);
			return new CloudUser(getMeta(resource), username, isAdmin, isActive, organizations, managed_organizations, audited_organizations, spaces, managed_spaces, audited_spaces);
			
		}else{
			List<CloudOrganization> organizations = getOrgListByRole("organizations", resource);
			List<CloudOrganization> managed_organizations = getOrgListByRole("managed_organizations", resource);
			List<CloudOrganization> audited_organizations = getOrgListByRole("audited_organizations", resource);
			
			List<CloudSpace> spaces = getSpaceListByRole("spaces", resource);
			List<CloudSpace> managed_spaces = getSpaceListByRole("managed_spaces", resource);
			List<CloudSpace> audited_spaces = getSpaceListByRole("audited_spaces", resource);
			return new CloudUser(getMeta(resource), getNameOfResource(resource), isAdmin, isActive,organizations, managed_organizations, audited_organizations, spaces, managed_spaces, audited_spaces);
		}
	}
	

	private CloudUserNoUaa mapUserNoUaaResource(Map<String, Object> resource) {
		String username = (String) getEntity(resource).get("username");
		
		List<CloudOrganization> organizations = getOrgListByRole("organizations", resource);
		
		List<CloudSpace> spaces = getSpaceListByRole("spaces", resource);
		
		return new CloudUserNoUaa(getMeta(resource), username, organizations, spaces);
		
	}
	
	private List<CloudOrganization> getOrgListByRole(String orgRole, Map<String, Object> resource){
		List<Map<String, Object>> orgListResource = getEmbeddedResourceList(getEntity(resource),orgRole);
		List<CloudOrganization> orgList = new ArrayList<CloudOrganization>();
		if (orgListResource != null) {
			for (Map<String, Object> orgMap : orgListResource) {
				CloudOrganization mapOrganizationResource = mapOrganizationResource(orgMap);
				orgList.add(mapOrganizationResource);
			}
		}else{
			return null;
		}
		return orgList;
	}
	
	private List<CloudSpace> getSpaceListByRole(String spaceRole, Map<String, Object> resource){
		List<Map<String, Object>> spaceListResource = getEmbeddedResourceList(getEntity(resource), spaceRole);
		List<CloudSpace> spaceList = new ArrayList<CloudSpace>();
		if (spaceListResource != null) {
			for (Map<String, Object> spaceMap : spaceListResource) {
				CloudSpace mapSpaceResource = mapSpaceResource(spaceMap);
				spaceList.add(mapSpaceResource);
			}
		}else{
			return null;
		}
		return spaceList;
	}

	private CloudSpace mapSpaceResource(Map<String, Object> resource) {
		Map<String, Object> organizationMap = getEmbeddedResource(resource, "organization");
		CloudOrganization organization = null;
		if (organizationMap != null) {
			organization = mapOrganizationResource(organizationMap);
		}
		return new CloudSpace(getMeta(resource), getNameOfResource(resource), organization);
	}

	private CloudOrganization mapOrganizationResource(
            Map<String, Object> resource) {
        Boolean billingEnabled = getEntityAttribute(resource,
                "billing_enabled", Boolean.class);
		Map<String, Object> quotaDefinition = getEmbeddedResource(resource,
                "quota_definition");
		List<CloudSpace> spaces = getSpaceListByRole("spaces", resource);
		CloudQuota quota = null;
		if (quotaDefinition != null) {
			quota = mapQuotaResource(quotaDefinition);
        }
		if(spaces != null){
			return new CloudOrganization(getMeta(resource),
                getNameOfResource(resource), quota,billingEnabled, spaces);
		}
        return new CloudOrganization(getMeta(resource),
                getNameOfResource(resource), quota,billingEnabled);
    }

    private CloudQuota mapQuotaResource(Map<String, Object> resource) {
        Boolean nonBasicServicesAllowed = getEntityAttribute(resource,
                "non_basic_services_allowed", Boolean.class);
        int totalServices = getEntityAttribute(resource, "total_services",
                Integer.class);
        int totalRoutes = 4;
        if (resource.get("total_routes") != null) {
        	totalRoutes = getEntityAttribute(resource, "total_routes",
                    Integer.class);
        }   
        long memoryLimit = getEntityAttribute(resource, "memory_limit",
                Long.class);

        return new CloudQuota(getMeta(resource), getNameOfResource(resource),
                nonBasicServicesAllowed, totalServices, totalRoutes,
                memoryLimit);
    }
    
    private CloudSpaceQuota mapSpaceQuotaResource(Map<String, Object> resource) {
    	Boolean nonBasicServicesAllowed = getEntityAttribute(resource,
                "non_basic_services_allowed", Boolean.class);
    	int totalServices = getEntityAttribute(resource, "total_services",
                Integer.class);
        int totalRoutes = 4;
        if (resource.get("total_routes") != null) {
        	totalRoutes = getEntityAttribute(resource, "total_routes",
                    Integer.class);
        }   
        long memoryLimit = getEntityAttribute(resource, "memory_limit",
                Long.class);
        String org_guid = getEntityAttribute(resource, "organization_guid", 
        		String.class);
        CloudSpaceQuota spaceQuota = new CloudSpaceQuota(getMeta(resource), getNameOfResource(resource)
        		, nonBasicServicesAllowed, totalServices, totalRoutes, memoryLimit, org_guid);
		return spaceQuota;
	}

	private CloudDomain mapDomainResource(Map<String, Object> resource) {
		@SuppressWarnings("unchecked")
		Map<String, Object> ownerResource = getEntityAttribute(resource, "owning_organization", Map.class);
		CloudOrganization owner;
		if (ownerResource == null) {
			owner = new CloudOrganization(CloudEntity.Meta.defaultMeta(), "none");
		} else {
			owner = mapOrganizationResource(ownerResource);
		}
		return new CloudDomain(getMeta(resource), getNameOfResource(resource), owner);
	}

	private CloudRoute mapRouteResource(Map<String, Object> resource) {
		@SuppressWarnings("unchecked")
		List<Object> apps = getEntityAttribute(resource, "apps", List.class);
		String host = getEntityAttribute(resource, "host", String.class);
		CloudDomain domain = mapDomainResource(getEmbeddedResource(resource, "domain"));
		return new CloudRoute(getMeta(resource), host, domain, apps.size());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private CloudApplication mapApplicationResource(Map<String, Object> resource) {
		CloudApplication app = new CloudApplication(
				getMeta(resource),
				getNameOfResource(resource));
		app.setInstances(getEntityAttribute(resource, "instances", Integer.class));
		app.setServices(new ArrayList<String>());
		app.setState(CloudApplication.AppState.valueOf(getEntityAttribute(resource, "state", String.class)));
		//TODO: debug
		app.setDebug(null);

		Integer runningInstancesAttribute = getEntityAttribute(resource, "running_instances", Integer.class);
		if (runningInstancesAttribute != null) {
			app.setRunningInstances(runningInstancesAttribute);
		}
		String command = getEntityAttribute(resource, "command", String.class);
		String buildpack = getEntityAttribute(resource, "buildpack", String.class);
		Map<String, Object> stackResource = getEmbeddedResource(resource, "stack");
		CloudStack stack = mapStackResource(stackResource);
		Integer healthCheckTimeout = getEntityAttribute(resource, "health_check_timeout", Integer.class);
		Staging staging = new Staging(command, buildpack, stack.getName(), healthCheckTimeout);
		app.setStaging(staging);

		Map envMap = getEntityAttribute(resource, "environment_json", Map.class);
		if (envMap.size() > 0) {
			app.setEnv(envMap);
		}
		app.setMemory(getEntityAttribute(resource, "memory", Integer.class));
		app.setDiskQuota(getEntityAttribute(resource, "disk_quota", Integer.class));
		List<Map<String, Object>> serviceBindings = getEntityAttribute(resource, "service_bindings", List.class);
		List<String> serviceList = new ArrayList<String>();
		for (Map<String, Object> binding : serviceBindings) {
			Map<String, Object> service = getEntityAttribute(binding, "service_instance", Map.class);
			String serviceName = getNameOfResource(service);
			if (serviceName != null) {
				serviceList.add(serviceName);
			}
		}
		app.setServices(serviceList);
		return app;
	}
	
	private CloudService mapServiceResource(Map<String, Object> resource) {
		CloudService cloudService = new CloudService(getMeta(resource), getNameOfResource(resource));
		Map<String, Object> servicePlanResource = getEmbeddedResource(resource, "service_plan");
		if (servicePlanResource != null) {
			cloudService.setPlan(getEntityAttribute(servicePlanResource, "name", String.class));

			Map<String, Object> serviceResource = getEmbeddedResource(servicePlanResource, "service");
			if (serviceResource != null) {
				cloudService.setLabel(getEntityAttribute(serviceResource, "label", String.class));
				cloudService.setProvider(getEntityAttribute(serviceResource, "provider", String.class));
				cloudService.setVersion(getEntityAttribute(serviceResource, "version", String.class));
			}
		}
		return cloudService;
	}

	@SuppressWarnings("unchecked")
	private CloudServiceInstance mapServiceInstanceResource(Map<String, Object> resource) {
		CloudServiceInstance serviceInstance = new CloudServiceInstance(getMeta(resource), getNameOfResource(resource));

		serviceInstance.setType(getEntityAttribute(resource, "type", String.class));
		serviceInstance.setDashboardUrl(getEntityAttribute(resource, "dashboard_url", String.class));
		serviceInstance.setCredentials(getEntityAttribute(resource, "credentials", Map.class));

		Map<String, Object> servicePlanResource = getEmbeddedResource(resource, "service_plan");
		serviceInstance.setServicePlan(mapServicePlanResource(servicePlanResource));

		CloudService service = mapServiceResource(resource);
		serviceInstance.setService(service);

		List<Map<String, Object>> bindingsResource = getEmbeddedResourceList(getEntity(resource), "service_bindings");
		List<CloudServiceBinding> bindings = new ArrayList<>(bindingsResource.size());
		for (Map<String, Object> bindingResource : bindingsResource) {
			bindings.add(mapServiceBinding(bindingResource));
		}
		serviceInstance.setBindings(bindings);

		return serviceInstance;
	}
	
	@SuppressWarnings("unchecked")
	private CloudServiceBinding mapServiceBinding(Map<String, Object> resource) {
		CloudServiceBinding binding = new CloudServiceBinding(getMeta(resource),
			getNameOfResource(resource));

		binding.setAppGuid(UUID.fromString(getEntityAttribute(resource, "app_guid", String.class)));
		binding.setSyslogDrainUrl(getEntityAttribute(resource, "syslog_drain_url", String.class));
		binding.setCredentials(getEntityAttribute(resource, "credentials", Map.class));
		binding.setBindingOptions(getEntityAttribute(resource, "binding_options", Map.class));

		return binding;
	}
	
	private CloudServiceOffering mapServiceOfferingResource(Map<String, Object> resource) {
		CloudServiceOffering cloudServiceOffering = new CloudServiceOffering(
				getMeta(resource),
				getEntityAttribute(resource, "label", String.class),
				getEntityAttribute(resource, "provider", String.class),
				getEntityAttribute(resource, "version", String.class),
				getEntityAttribute(resource, "description", String.class),
				getEntityAttribute(resource, "active", Boolean.class),
				getEntityAttribute(resource, "bindable", Boolean.class),
				getEntityAttribute(resource, "url", String.class),
				getEntityAttribute(resource, "info_url", String.class),
				getEntityAttribute(resource, "unique_id", String.class),
				getEntityAttribute(resource, "extra", String.class),
				getEntityAttribute(resource, "documentation_url", String.class));
		List<Map<String, Object>> servicePlanList = getEmbeddedResourceList(getEntity(resource), "service_plans");
		if (servicePlanList != null) {
			for (Map<String, Object> servicePlanResource : servicePlanList) {
				CloudServicePlan servicePlan = mapServicePlanResource(servicePlanResource);
				servicePlan.setServiceOffering(cloudServiceOffering);
				cloudServiceOffering.addCloudServicePlan(servicePlan);
			}
		}
		return cloudServiceOffering;
	}
	
	private CloudServicePlan mapServicePlanResource(Map<String, Object> servicePlanResource) {
		Boolean publicPlan = getEntityAttribute(servicePlanResource, "public", Boolean.class);

		return new CloudServicePlan(
				getMeta(servicePlanResource),
				getEntityAttribute(servicePlanResource, "name", String.class),
				getEntityAttribute(servicePlanResource, "description", String.class),
				getEntityAttribute(servicePlanResource, "free", Boolean.class),
				publicPlan == null ? true : publicPlan,
				getEntityAttribute(servicePlanResource, "extra", String.class),
				getEntityAttribute(servicePlanResource, "unique_id", String.class));
	}

	private CloudServiceBroker mapServiceBrokerResource(Map<String, Object> resource) {
		return new CloudServiceBroker(getMeta(resource),
			getEntityAttribute(resource, "name", String.class),
			getEntityAttribute(resource, "broker_url", String.class),
			getEntityAttribute(resource, "auth_username", String.class));
	}

	private CloudStack mapStackResource(Map<String, Object> resource) {
		return new CloudStack(getMeta(resource),
				getNameOfResource(resource),
				getEntityAttribute(resource, "description", String.class));
	}

	@SuppressWarnings("unchecked")
	public static CloudEntity.Meta getMeta(Map<String, Object> resource) {
		UUID guid = null;
		Map<String, Object> metadata = (Map<String, Object>) resource.get("metadata");
		if (String.valueOf(metadata.get("guid")).contains("-")){
			guid = UUID.fromString(String.valueOf(metadata.get("guid")));
		}		
		Date createdDate = parseDate(String.valueOf(metadata.get("created_at")));
		Date updatedDate = parseDate(String.valueOf(metadata.get("updated_at")));
		return new CloudEntity.Meta(guid, createdDate, updatedDate);
	}

	private static Date parseDate(String dateString) {
		if (dateString != null) {
			try {
				// if the time zone part of the dateString contains a colon (e.g. 2013-09-19T21:56:36+00:00)
				// then remove it before parsing
				String isoDateString = dateString.replaceFirst(":(?=[0-9]{2}$)", "").replaceFirst("Z$", "+0000");
				return dateFormatter.parse(isoDateString);
			} catch (Exception ignore) {}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEntity(Map<String, Object> resource) {
		return (Map<String, Object>) resource.get("entity");
	}

	@SuppressWarnings("unchecked")
	public static <T> T getEntityAttribute(Map<String, Object> resource, String attributeName, Class<T> targetClass) {
		if (resource == null) {
			return null;
		}
		Map<String, Object> entity = (Map<String, Object>) resource.get("entity");
		Object attributeValue = entity.get(attributeName);
		if (attributeValue == null) {
			return null;
		}
		if (targetClass == String.class) {
			return (T) String.valueOf(attributeValue);
		}
		if (targetClass == Long.class) {
            return (T) Long.valueOf(String.valueOf(attributeValue));
        }
		if (targetClass == Integer.class || targetClass == Boolean.class || targetClass == Map.class || targetClass == List.class) {
			return (T) attributeValue;
		}
		if (targetClass == UUID.class && attributeValue instanceof String) {
			return (T) UUID.fromString((String)attributeValue);
		}
		throw new IllegalArgumentException(
				"Error during mapping - unsupported class for attribute mapping " + targetClass.getName());
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmbeddedResource(Map<String, Object> resource, String embeddedResourceName) {
		Map<String, Object> entity = (Map<String, Object>) resource.get("entity");
		return (Map<String, Object>) entity.get(embeddedResourceName);
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getEmbeddedResourceList(Map<String, Object> resource, String embeddedResourceName) {
		return (List<Map<String, Object>>) resource.get(embeddedResourceName);
	}
}
