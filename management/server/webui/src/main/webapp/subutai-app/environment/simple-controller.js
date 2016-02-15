'use strict';

angular.module('subutai.environment.simple-controller', [])
	.controller('EnvironmentSimpleViewCtrl', EnvironmentSimpleViewCtrl);

EnvironmentSimpleViewCtrl.$inject = ['$scope', 'environmentService', 'trackerSrv', 'SweetAlert', 'ngDialog', '$timeout'];

function EnvironmentSimpleViewCtrl($scope, environmentService, trackerSrv, SweetAlert, ngDialog, $timeout) {

	var vm = this;
	var GRID_CELL_SIZE = 100;
	var GRID_SIZE = 100;

	vm.popupLogState = 'full';

	vm.currentEnvironment = {};
	vm.currentTemplate = {};
	vm.buildEnvironment = buildEnvironment;
	vm.editEnvironment = editEnvironment;
	vm.notifyChanges = notifyChanges;
	vm.applyChanges = applyChanges;
	vm.getQuotaColor = getQuotaColor;

	vm.environments = [];
	vm.domainStrategies = [];
	vm.strategies = [];

	vm.peerIds = [];
	vm.colors = quotaColors;
	vm.templates = [];

	vm.activeCloudTab = 'templates';

	vm.templateGrid = [];
	vm.cubeGrowth = 1;
	vm.environment2BuildName = 'Environment name';

	// functions

	vm.addNewNode = addNewNode;
	vm.removeNodeGroup = removeNodeGroup;
	vm.setNodeData = setNodeData;
	vm.setupAdvancedEnvironment = setupAdvancedEnvironment;
	vm.initJointJs = initJointJs;
	vm.buildEnvironmentByJoint = buildEnvironmentByJoint;
	vm.clearWorkspace = clearWorkspace;
	vm.addSettingsToTemplate = addSettingsToTemplate;

	vm.addContainer = addContainer;

	environmentService.getTemplates()
		.success(function (data) {
			vm.templates = data;
		})
		.error(function (data) {
			VARS_MODAL_ERROR( SweetAlert, 'Error on getting templates ' + data );
		});

	environmentService.getStrategies().success(function (data) {
		vm.strategies = data;
	});

	environmentService.getDomainStrategies().success(function (data) {
		vm.domainStrategies = data;
	});

	environmentService.getPeers().success(function (data) {
		vm.peerIds = data;
	});

	function getLogsFromTracker(environmentId) {
		trackerSrv.getOperations('ENVIRONMENT MANAGER', moment().format('YYYY-MM-DD'), moment().format('YYYY-MM-DD'), 100)
			.success(function (data) {
				for(var i = 0; i < data.length; i++) {
					if(data[i].description.includes(environmentId)) {
						console.log(data[i]);
						getLogById(data[i].id, true);
						break;
					}
				}
				return false;
			}).error(function(error) {
				console.log(error);
			});		
	}

	function checkLastLog(status) {
		var lastLog = vm.logMessages[vm.logMessages.length - 1];
		lastLog.time = moment().format('HH:mm:ss');
		if(status === true) {
			lastLog.status = 'success';
			lastLog.classes = ['fa-check', 'g-text-green'];
		} else {
			lastLog.status = 'success';
			lastLog.classes = ['fa-times', 'g-text-red'];
		}
	}

	function getLogById(id, checkLast, prevLogs) {
		if(checkLast === undefined || checkLast === null) checkLast = false;
		if(prevLogs === undefined || prevLogs === null) prevLogs = false;
		trackerSrv.getOperation('ENVIRONMENT MANAGER', id)
			.success(function (data) {
				if(data.state == 'RUNNING') {

					if(checkLast) {
						checkLastLog(true);
					}

					var logs = data.log.split(/(?:\r\n|\r|\n)/g);
					var result = [];
					var i = 0;
					if(prevLogs) {
						i = prevLogs.length;
						if(logs.length > prevLogs.length) {
							checkLastLog(true);
						}
					}
					for(i; i < logs.length; i++) {

						var logTime = moment().format('HH:mm:ss');
						var logStatus = 'success';
						var logClasses = ['fa-check', 'g-text-green'];
						if(i+1 == logs.length) {
							logTime = '';
							logStatus = 'in-progress';
							logClasses = ['fa-spinner', 'fa-pulse'];
						}

						var  currentLog = {
							"time": logTime,
							"status": logStatus,
							"classes": logClasses,
							"text": logs[i]
						};
						result.push(currentLog);
					}

					vm.logMessages = vm.logMessages.concat(result);

					setTimeout(function() {
						getLogById(id, false, logs);
					}, 2000);					

					return result;
				} else {
					if(data.state == 'FAILED') {
						checkLastLog(false);
					} else {
						SweetAlert.swal("Success!", "Your environment has been built successfully.", "success");
						checkLastLog(true);
					}
				}
			}).error(function(error) {
				console.log(error);
			});
	}

	vm.logMessages = [];
	function buildEnvironment() {
		vm.buildStep = 'showLogs';

		vm.logMessages = [];
		var currentLog = {
			"time": '',
			"status": 'in-progress',
			"classes": ['fa-spinner', 'fa-pulse'],
			"text": 'Registering environment'
		};
		vm.logMessages.push(currentLog);

		environmentService.startEnvironmentAutoBuild(vm.environment2BuildName, JSON.stringify(vm.containers2Build))
			.success(function(data){
				vm.newEnvID = data;
				currentLog.status = 'success';
				currentLog.classes = ['fa-check', 'g-text-green'];
				currentLog.time = moment().format('HH:mm:ss');

				currentLog = {
					"time": '',
					"status": 'in-progress',
					"classes": ['fa-spinner', 'fa-pulse'],
					"text": 'Environment creation has been started'
				};
				vm.logMessages.push(currentLog);

				//var logId = getLogsFromTracker(vm.newEnvID);
				var logId = getLogsFromTracker(vm.environment2BuildName);

			}).error(function(error){
				if(error && error.ERROR === undefined) {
					VARS_MODAL_ERROR( SweetAlert, 'Error: ' + error );
				} else {
					VARS_MODAL_ERROR( SweetAlert, 'Error: ' + error.ERROR );
				}
				currentLog.status = 'fail';
				currentLog.classes = ['fa-times', 'g-text-red'];
				currentLog.time = moment().format('HH:mm:ss');				
			});
	}

	function notifyChanges() {
		vm.currentEnvironment.excludedContainersByQuota =
			getSortedContainersByQuota(vm.currentEnvironment.excludedContainers);
		vm.currentEnvironment.includedContainersByQuota =
			getSortedContainersByQuota(vm.currentEnvironment.includedContainers);

		ngDialog.open({
			template: 'subutai-app/environment/partials/popups/environment-modification-info.html',
			scope: $scope,
			className: 'b-build-environment-info'
		});
	}

	function getSortedContainersByQuota(containers) {
		var sortedContainers = containers.length > 0 ? {} : null;
		for (var index = 0; index < containers.length; index++) {
			var quotaSize = containers[index].attributes.quotaSize;
			var templateName = containers[index].attributes.templateName;
			if (!sortedContainers[templateName]) {
				sortedContainers[templateName] = {};
				sortedContainers[templateName].quotas = {};
				sortedContainers[templateName]
					.quotas[quotaSize] = 1;
			} else {
				if (!sortedContainers[templateName].quotas) {
					sortedContainers[templateName].quotas = {};
					sortedContainers[templateName]
						.quotas[quotaSize] = 1;
				} else {
					if (!sortedContainers[templateName].quotas[quotaSize]) {
						sortedContainers[templateName]
							.quotas[quotaSize] = 1;
					} else {
						sortedContainers[containers[index].attributes.templateName]
							.quotas[quotaSize] += 1;
					}
				}
			}
		}
		return sortedContainers;
	}

	function applyChanges() {
		vm.isApplyingChanges = true;
		ngDialog.closeAll();

		var excludedContainers = [];
		for (var i = 0; i < vm.currentEnvironment.excludedContainers.length; i++) {
			excludedContainers.push(vm.currentEnvironment.excludedContainers[i].get('id'));
		}
		var includedContainers = [];
		for (var i = 0; i < vm.currentEnvironment.includedContainers.length; i++) {
			includedContainers.push({
				"size": vm.currentEnvironment.includedContainers[i].get('quotaSize'),
				"templateName": vm.currentEnvironment.includedContainers[i].get('templateName'),
				"name": vm.currentEnvironment.includedContainers[i].get('containerName'),
				"position": vm.currentEnvironment.includedContainers[i].get('position')
			});
		}
		vm.currentEnvironment.modificationData = {
			included: includedContainers,
			excluded: excludedContainers,
			environmentId: vm.currentEnvironment.id
		};

		ngDialog.open({
			template: 'subutai-app/environment/partials/popups/environment-modification-status.html',
			scope: $scope,
			className: 'b-build-environment-info'
		});

		vm.currentEnvironment.modifyStatus = 'modifying';
		environmentService.modifyEnvironment(vm.currentEnvironment.modificationData).success(function (data) {
			vm.currentEnvironment.modifyStatus = 'modified';
			clearWorkspace();
			vm.isEditing = false;
			vm.isApplyingChanges = false;
		}).error(function (data) {
			vm.currentEnvironment.modifyStatus = 'error';
			clearWorkspace();
			vm.isEditing = false;
			vm.isApplyingChanges = false;
		});
	}

	function editEnvironment(environment) {
		vm.clearWorkspace();
		vm.isApplyingChanges = false;
		vm.currentEnvironment = environment;
		vm.currentEnvironment.excludedContainers = [];
		vm.currentEnvironment.includedContainers = [];
		vm.isEditing = true;
		for(var container in environment.containers) {
			var pos = vm.findEmptyCubePostion();
			var devElement = new joint.shapes.tm.devElement({
				position: { x: (GRID_CELL_SIZE * pos.x) + 20, y: (GRID_CELL_SIZE * pos.y) + 20 },
				templateName: environment.containers[container].templateName,
				quotaSize: environment.containers[container].type,
				hostname: environment.containers[container].hostname,
				containerId: environment.containers[container].id,
				attrs: {
					image: { 'xlink:href': 'assets/templates/' + environment.containers[container].templateName + '.jpg' },
					title: {text: environment.containers[container].templateName}
				}
			});
			graph.addCell(devElement);
		}
	}

	function addNewNode() {
		if(vm.nodeStatus == 'Add to') {
			var tempNode = vm.advancedEnv.currentNode;

			if(tempNode === undefined) return;
			if(tempNode.name === undefined || tempNode.name.length < 1) return;
			if(tempNode.numberOfContainers === undefined || tempNode.numberOfContainers < 1) return;
			if(tempNode.sshGroupId === undefined) return;
			if(tempNode.hostsGroupId === undefined) return;

			if( jQuery.grep( vm.nodeList, function( i ) {
					return tempNode.name == i.name;
				}).length != 0
			) return;

			vm.nodeList.push(tempNode);
		} else {
			vm.nodeStatus = 'Add to';
		}


		vm.advancedEnv.currentNode = angular.copy( vm.advancedEnv.currentNode );
		vm.advancedEnv.currentNode.name = "";
	}

	function setNodeData(key) {
		vm.nodeStatus = 'Update in';
		vm.advancedEnv.currentNode = vm.nodeList[key];
	}

	function removeNodeGroup(key)
	{
		vm.nodeList.splice(key, 1);
	}

	function getDefaultValues() {
		var defaultVal = {
			'templateName': 'master',
			'numberOfContainers': 2,
			'sshGroupId': 0,
			'hostsGroupId': 0,
			'type': 'TINY'
		};
		return defaultVal;
	}

	function setupAdvancedEnvironment() {
		if(vm.advancedEnv.name === undefined) return;
		if(vm.nodeList === undefined || vm.nodeList.length == 0) return;

		var finalEnvironment = vm.advancedEnv;
		finalEnvironment.nodeGroups = vm.nodeList;
		if(finalEnvironment.currentNod !== undefined) {
			finalEnvironment.nodeGroups.push(finalEnvironment.currentNode);
		}
		delete finalEnvironment.currentNode;

		var cloneContainers = {};

		for( var i = 0; i < finalEnvironment.nodeGroups.length; i++ )
		{
			var node = finalEnvironment.nodeGroups[i];
			for( var j = 0; j < node.numberOfContainers; j++ )
			{
				if( j < 0 ) break;

				if( cloneContainers[node.peerId] === undefined )
				{
					cloneContainers[node.peerId] = [];
				}

				cloneContainers[node.peerId].push(node);
			}
		}

		console.log(cloneContainers);
		LOADING_SCREEN();
		ngDialog.closeAll();
		environmentService.setupAdvancedEnvironment(finalEnvironment.name, cloneContainers)
			.success(function(data){
				console.log(data);
				loadEnvironments();
				LOADING_SCREEN('none');
			}).error(function(error){
				console.log(error);
				LOADING_SCREEN('none');
			});

		vm.nodeList = [];
		vm.advancedEnv = {};
		vm.advancedEnv.currentNode = getDefaultValues();
	}

	var graph = new joint.dia.Graph;
	
	//custom shapes
	joint.shapes.tm = {};

	//simple creatiom templates
	joint.shapes.tm.toolElement = joint.shapes.basic.Generic.extend({

		toolMarkup: [
			'<g class="element-tools">',
				'<g class="element-tool-remove">',
					'<circle fill="#F8FBFD" r="8" stroke="#dcdcdc"/>',
					'<polygon transform="scale(1.2) translate(-5, -5)" fill="#292F6C" points="8.4,2.4 7.6,1.6 5,4.3 2.4,1.6 1.6,2.4 4.3,5 1.6,7.6 2.4,8.4 5,5.7 7.6,8.4 8.4,7.6 5.7,5 "/>',
					'<title>Remove</title>',
				'</g>',
				/*'<g class="element-call-menu">',
					'<circle fill="#F8FBFD" r="8" stroke="#dcdcdc"/>',
					'<polygon transform="scale(1.2) translate(-5, -5)" fill="#292F6C" points="8.4,2.4 7.6,1.6 5,4.3 2.4,1.6 1.6,2.4 4.3,5 1.6,7.6 2.4,8.4 5,5.7 7.6,8.4 8.4,7.6 5.7,5 "/>',
					'<title>Menu</title>',
				'</g>',*/
			'</g>'
		].join(''),

		defaults: joint.util.deepSupplement({
			attrs: {
				text: { 'font-weight': 400, 'font-size': 'small', fill: 'black', 'text-anchor': 'middle', 'ref-x': .5, 'ref-y': .5, 'y-alignment': 'middle' },
				'g.element-call-menu': {'ref-x': 18, 'ref-y': 25}
			},
		}, joint.shapes.basic.Generic.prototype.defaults)

	});

	joint.shapes.tm.devElement = joint.shapes.tm.toolElement.extend({

		markup: [
			'<g class="rotatable">',
				'<g class="scalable">',
					'<rect class="b-border"/>',
				'</g>',
				'<title/>',
				'<image/>',
				'<rect class="b-magnet"/>',
			'</g>'
		].join(''),

		defaults: joint.util.deepSupplement({
			type: 'tm.devElement',
			size: { width: 70, height: 70 },
			attrs: {
				title: {text: 'Static Tooltip'},
				'rect.b-border': {fill: '#fff', stroke: '#dcdcdc', 'stroke-width': 1, width: 70, height: 70, rx: 50, ry: 50},
				'rect.b-magnet': {fill: '#04346E', width: 10, height: 10, rx: 50, ry: 50, magnet: true, transform: 'translate(30,53)'},
				image: {'ref-x': 9, 'ref-y': 9, ref: 'rect', width: 50, height: 50},
			}
		}, joint.shapes.tm.toolElement.prototype.defaults)
	});

	//custom view
	joint.shapes.tm.ToolElementView = joint.dia.ElementView.extend({
		initialize: function() {
			joint.dia.ElementView.prototype.initialize.apply(this, arguments);
		},
		render: function () {
			joint.dia.ElementView.prototype.render.apply(this, arguments);
			this.renderTools();
			this.update();
			return this;
		},
		renderTools: function () {
			var toolMarkup = this.model.toolMarkup || this.model.get('toolMarkup');
			if (toolMarkup) {
				var nodes = V(toolMarkup);
				V(this.el).append(nodes);
			}
			return this;
		},
		mouseover: function(evt, x, y) {
		},
		pointerclick: function (evt, x, y) {
			this._dx = x;
			this._dy = y;
			this._action = '';
			var className = evt.target.parentNode.getAttribute('class');
			switch (className) {
				case 'element-tool-remove':
					if (this.model.attributes.containerId) {
						vm.currentEnvironment.excludedContainers.push(this.model);
						$('.js-add-dev-element[data-type=' + this.model.attributes.devType + ']')
							.removeClass('b-devops-menu__li-link_active');
						this.model.remove();
						$('.js-devops-item-info-block').hide();
						delete vm.templateGrid[Math.floor(x / GRID_CELL_SIZE)][Math.floor(y / GRID_CELL_SIZE)];
					} else {
						var object =
							vm.currentEnvironment.includedContainers ?
								getElementByField('id', this.model.id, vm.currentEnvironment.includedContainers) :
								null;
						object !== null ? vm.currentEnvironment.includedContainers.splice(object.index, 1): null;
						$('.js-add-dev-element[data-type=' + this.model.attributes.devType + ']')
							.removeClass('b-devops-menu__li-link_active');
						this.model.remove();
						$('.js-devops-item-info-block').hide();
						delete vm.templateGrid[Math.floor(x / GRID_CELL_SIZE)][Math.floor(y / GRID_CELL_SIZE)];
					}
					return;
					break;
				case 'element-call-menu':
					var elementPos = this.model.get('position');
					$('.js-dropen-menu').css({
						'left': (elementPos.x + 70) + 'px',
						'top': (elementPos.y + 83) + 'px',
						'display': 'block'
					});
					return;
					break;
				case 'rotatable':
					if(this.model.attributes.containerId) {
						return;
					}
					vm.currentTemplate = this.model;
					ngDialog.open({
						template: 'subutai-app/environment/partials/popups/templateSettings.html',
						scope: $scope
					});
					return;
					break;
				default:
			}
			joint.dia.CellView.prototype.pointerclick.apply(this, arguments);
		}
	});
	joint.shapes.tm.devElementView = joint.shapes.tm.ToolElementView;

	var containerCounter = 1;
	function addContainer(template, $event) {

		var pos = findEmptyCubePostion();
		var img = $($event.currentTarget).find('img');

		var devElement = new joint.shapes.tm.devElement({
			position: { x: (GRID_CELL_SIZE * pos.x) + 20, y: (GRID_CELL_SIZE * pos.y) + 20 },
			templateName: template,
			quotaSize: 'SMALL',
			containerName: 'Container ' + (containerCounter++).toString(),
			attrs: {
				image: { 'xlink:href': img.attr('src') },
				'rect.b-magnet': {fill: vm.colors['SMALL']},
				title: {text: $(this).data('template')}
			}
		});
		vm.isEditing ? vm.currentEnvironment.includedContainers.push(devElement) : null;
		graph.addCell(devElement);
		return false;
	}

	function findEmptyCubePostion() {
		for( var j = 0; j < vm.cubeGrowth; j++ ) {
			for( var i = 0; i < vm.cubeGrowth; i++ ) {
				if( vm.templateGrid[i] === undefined ) {
					vm.templateGrid[i] = new Array();
					vm.templateGrid[i][j] = 1;

					return {x:i, y:j};
				}

				if( vm.templateGrid[i][j] !== 1 ) {
					vm.templateGrid[i][j] = 1;
					return {x:i, y:j};
				}
			}
		}

		vm.templateGrid[vm.cubeGrowth] = new Array();
		vm.templateGrid[vm.cubeGrowth][0] = 1;
		vm.cubeGrowth++;
		return { x : vm.cubeGrowth - 1, y : 0 };
	}

	vm.findEmptyCubePostion = findEmptyCubePostion;	

	function initJointJs() {

		var paper = new joint.dia.Paper({
			el: $('#js-environment-creation'),
			width: '100%',
			height: '100%',
			model: graph,
			gridSize: 1
		});

		var p0;
		paper.on('cell:pointerdown', function(cellView) {
			p0 = cellView.model.get('position');
		});

		paper.on('cell:pointerup',
			function(cellView, evt, x, y) {

				var pos = cellView.model.get('position');
				var p1 = { x: g.snapToGrid(pos.x, GRID_CELL_SIZE) + 20, y: g.snapToGrid(pos.y, GRID_CELL_SIZE) + 20 };

				var i = Math.floor( p1.x / GRID_CELL_SIZE );
				var j = Math.floor( p1.y / GRID_CELL_SIZE );

				if( vm.templateGrid[i] === undefined )
				{
					vm.templateGrid[i] = new Array();
				}

				if( vm.templateGrid[i][j] !== 1 )
				{
					vm.templateGrid[i][j] = 1;
					cellView.model.set('position', p1);
					vm.cubeGrowth = vm.cubeGrowth < ( i + 1 ) ? ( i + 1 ) : vm.cubeGrowth;
					vm.cubeGrowth = vm.cubeGrowth < ( j + 1 ) ? ( j + 1 ) : vm.cubeGrowth;

					i = Math.floor( p0.x / GRID_CELL_SIZE );
					j = Math.floor( p0.y / GRID_CELL_SIZE );

					delete vm.templateGrid[i][j];
				}
				else
					cellView.model.set('position', p0);
			}
		);

		$('.js-scrollbar').perfectScrollbar();

		//zoom on scroll
		/*paper.$el.on('mousewheel DOMMouseScroll', onMouseWheel);

		function onMouseWheel(e) {

			e.preventDefault();
			e = e.originalEvent;

			var delta = Math.max(-1, Math.min(1, (e.wheelDelta || -e.detail))) / 50;
			var offsetX = (e.offsetX || e.clientX - $(this).offset().left); // offsetX is not defined in FF
			var offsetY = (e.offsetY || e.clientY - $(this).offset().top); // offsetY is not defined in FF
			var p = offsetToLocalPoint(offsetX, offsetY);
			var newScale = V(paper.viewport).scale().sx + delta; // the current paper scale changed by delta

			if (newScale > 0.4 && newScale < 2) {
				paper.setOrigin(0, 0); // reset the previous viewport translation
				paper.scale(newScale, newScale, p.x, p.y);
			}
		}

		function offsetToLocalPoint(x, y) {
			var svgPoint = paper.svg.createSVGPoint();
			svgPoint.x = x;
			svgPoint.y = y;
			// Transform point into the viewport coordinate system.
			var pointTransformed = svgPoint.matrixTransform(paper.viewport.getCTM().inverse());
			return pointTransformed;
		}*/
	}

	vm.buildStep = 'confirm';
	function buildEnvironmentByJoint() {

		vm.newEnvID = [];		

		var allElements = graph.getCells();
		vm.env2Build = {};
		vm.containers2Build = [];
		vm.buildStep = 'confirm';

		for(var i = 0; i < allElements.length; i++) {
			var currentElement = allElements[i];
			var container2Build = {
				"size": currentElement.get('quotaSize'),
				"templateName": currentElement.get('templateName'),
				"name": currentElement.get('containerName'),
				"position": currentElement.get('position')
			};

			if (vm.env2Build[currentElement.get('templateName')] === undefined) {
				vm.env2Build[currentElement.get('templateName')] = {};
				vm.env2Build[currentElement.get('templateName')].count = 1;
				vm.env2Build[currentElement.get('templateName')]
					.sizes = {};
				vm.env2Build[currentElement.get('templateName')]
					.sizes[currentElement.get('quotaSize')] = 1;
			} else {
				vm.env2Build[currentElement.get('templateName')].count++;
				if(vm.env2Build[currentElement.get('templateName')].sizes[currentElement.get('quotaSize')] === undefined) {
					vm.env2Build[currentElement.get('templateName')]
						.sizes[currentElement.get('quotaSize')] = 1;
				} else {
					vm.env2Build[currentElement.get('templateName')]
						.sizes[currentElement.get('quotaSize')]++;
				}
			}

			vm.containers2Build.push(container2Build);
		}

		console.log(vm.containers2Build);
		ngDialog.open({
			template: 'subutai-app/environment/partials/popups/environment-build-info.html',
			scope: $scope,
			className: 'b-build-environment-info'
		});
	}

	function editEnvironment(environment) {
		clearWorkspace();
		vm.isApplyingChanges = false;
		vm.currentEnvironment = environment;
		vm.currentEnvironment.excludedContainers = [];
		vm.currentEnvironment.includedContainers = [];
		vm.isEditing = true;
		for(var container in environment.containers) {
			var pos = vm.findEmptyCubePostion();
			var devElement = new joint.shapes.tm.devElement({
				position: { x: (GRID_CELL_SIZE * pos.x) + 20, y: (GRID_CELL_SIZE * pos.y) + 20 },
				templateName: environment.containers[container].templateName,
				quotaSize: environment.containers[container].type,
				hostname: environment.containers[container].hostname,
				containerId: environment.containers[container].id,
				attrs: {
					image: { 'xlink:href': 'assets/templates/' + environment.containers[container].templateName + '.jpg' },
					title: {text: environment.containers[container].templateName}
				}
			});
			graph.addCell(devElement);
		}
	}

	function clearWorkspace() {
		vm.isEditing = false;
		vm.cubeGrowth = 0;
		vm.templateGrid = [];
		graph.resetCells();
	}

	function addSettingsToTemplate(settings) {
		vm.currentTemplate.set('quotaSize', settings.quotaSize);
		vm.currentTemplate.attr('rect.b-magnet/fill', vm.colors[settings.quotaSize]);
		vm.currentTemplate.set('containerName', settings.containerName);
		ngDialog.closeAll();
	}

	function getElementByField(field, value, collection) {
		for(var index = 0; index < collection.length; index++) {
			if(collection[index][field] === value) {
				return {
					container: collection[index],
					index: index
				};
			}
		}
		return null;
	}

	function getQuotaColor(quotaSize) {
		return quotaColors[quotaSize];
	}
}
