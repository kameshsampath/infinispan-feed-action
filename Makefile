PACKAGE_NAME:=redhatdevelopers
FEED_ACTION_NAME:=$(PACKAGE_NAME)/infinispan-feed
TRIGGER_NAME:=cacheEntryTrigger
INFINISPAN_SERVER_HOST:="infinispan-app-hotrod"
INFINISPAN_HOT_ROD_PORT:="11222"
CACHE_NAME:="default"

.PHONY: build
build:
	mvn clean package

.PHONY: create-package
create-package:
	wsk -i package update $(PACKAGE_NAME) -d "Demo package"

.PHONY: create-feed-trigger
create-feed-trigger:
	wsk -i trigger create $(TRIGGER_NAME) --feed $(FEED_ACTION_NAME) \
		-p hotrod_server_host $(INFINISPAN_SERVER_HOST) \
		-p hotrod_port $(INFINISPAN_HOT_ROD_PORT) \
		-p cache_name $(CACHE_NAME)

.PHONY: delete-feed-trigger
delete-feed-trigger:
	wsk -i trigger delete $(TRIGGER_NAME)