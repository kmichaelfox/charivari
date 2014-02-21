GrainOrganismEngine {
	var <>organismQueue,
	<>queueSequencer,
	<>automation,
	<>length,
	<>width,
	<>posX,
	<>posY,
	<>stateProbability,
	<>limitingThreshold,
	<>populationFloor,
	<>decline;

	*new { |seeds = 10, length = 80, width = 45|
		^super.new.init(seeds, length, width);
	}

	init { |seeds, length, width|
		this.length = length;
		this.width = width;

		// initialize organismQueue as a list
		this.organismQueue = List.new;

		// create dimensions of organismQueue (length x width dimension list of lists)
		this.length.do({ |i|
			organismQueue.add(List.new);
			this.width.do({organismQueue.at(i).add(nil)});
		});

		// initialize queueSequencer as a Pattern Sequencer
		this.queueSequencer = Pseq(organismQueue, inf).asStream;


		// create action state probability Dictionary
		this.stateProbability = Dictionary.new(5);
		this.decline = false;
		this.decline.value.postln;

		// initialize a limiting population threshold
		this.limitingThreshold = (350.rand + 50);

		// initialize a population floor
		this.populationFloor = (25.rand);

		// set initial probabilities (slanted towards growth)
		this.stateProbability.put(\die, 20);
		this.stateProbability.put(\reproduce, 45);
		this.stateProbability.put(\mutate, 20);
		this.stateProbability.put(\move, 10);
		this.stateProbability.put(\standby, 5);

		// create seed organisms
		this.addSeeds(seeds);

		// initialize position counters
		this.posX = 0;
		this.posY = 0;

		// initialize automation, which parses the list of organisms
		this.automation = Routine({
			loop({
				var currentRow = this.queueSequencer.next,
				currentRowSeq = Pseq(currentRow, 1).asStream;
				currentRow.size.do({
					var currentOrganism = currentRowSeq.next;
					if (currentOrganism != nil, {
						this.action(currentOrganism);
					});
					this.posY = (this.posY+1)%45;
				});

				this.posX = (this.posX+1)%80;

				if(this.isEmpty, {
					this.addSeeds(10);
					"scene is empty!".postln;
				});

				0.1.wait;
			})
		}).play;
	}

	printContents {
		var nodeCount = 0;
		length.do({|i|
			width.do({|j|
				var temp = this.organismQueue.at(i).at(j);
				if(temp != nil, {
					nodeCount = nodeCount + 1;
					nodeCount.post;
					"  ".post;
					"Node at: (".post;
					temp.posX.value.post;
					", ".post;
					temp.posY.value.post;
					")  ".post;
					temp.nodes.size.value.post;
					" nodes in chain".postln;
				})
			});
		});
	}

	addSeeds { |seeds|
		seeds.do({
			var x = this.length.rand, y = this.width.rand;
			this.organismQueue.at(x).put(y, GrainOrganism.new(posX: x, posY: y));
		});
	}

	action { |organism|
		// choose one of five behaviors (based on probability):
		// 0 - move
		// 1 - reproduce
		// 2 - mutate
		// 3 - die
		// 4 - standby
		var actTemp = 0;
		var actTempRand = 100.rand;
		//"dice roll: ".post;
		//actTempRand.postln;

		if(actTempRand > this.stateProbability.at(\move), {
			actTemp = actTemp + 1;
			actTempRand = actTempRand - this.stateProbability.at(\move);
			if(actTempRand > this.stateProbability.at(\reproduce), {
				actTemp = actTemp + 1;
				actTempRand = actTempRand - this.stateProbability.at(\reproduce);
				if(actTempRand > this.stateProbability.at(\mutate), {
					actTemp = actTemp + 1;
					actTempRand = actTempRand - this.stateProbability.at(\mutate);
					if(actTempRand > this.stateProbability.at(\die), {
						actTemp = actTemp + 1;
					});
				});
			});
		});
		//"choice: ".post;
		//actTemp.postln;

		switch(actTemp)
		{0}{ // move
			this.createNeighbor(organism);
			this.killOrganism(organism);
		}
		{1}{ // reproduce
			this.createNeighbor(organism);
		}
		{2}{ // mutate
			organism.mutate;
		}
		{3}{ // die
			this.killOrganism(organism);
		}
		{4}{ // standby
			// "standying by".postln;
		};

		if((GrainOrganism.numOrganisms > this.limitingThreshold).and(this.decline.not), {
			"hitting ceiling, starting decline".postln;
			this.decline_(true);
			this.populationFloor_(25.rand);
			this.stateProbability.removeAt(\die);
			this.stateProbability.removeAt(\reproduce);
			this.stateProbability.put(\die,45);
			this.stateProbability.put(\reproduce,20);
			"clear".postln;
		});

		if((GrainOrganism.numOrganisms < this.populationFloor).and(this.decline), {
			"hitting floor, starting growth".postln;
			this.decline_(false);
			this.limitingThreshold_(350.rand + 50);
			this.stateProbability.removeAt(\die);
			this.stateProbability.removeAt(\reproduce);
			this.stateProbability.put(\die,20);
			this.stateProbability.put(\reproduce,45);
		});
	}

	isEmpty {
		var boolFlag = false;
		length.do({|i|
			width.do({|j|
				var temp = this.organismQueue.at(i).at(j);
				boolFlag = boolFlag || (temp != nil);
			});
		});
		^boolFlag.not;
	}

	killOrganism { |organism|
		organism.die;
		this.organismQueue.at(organism.posX).put(organism.posY, nil);
	}

	getNeighbors { |x, y|
		var neighbors = List.new;
		if(x > 0, {
			if(y > 0, {
				neighbors.add([(x-1),(y-1),(organismQueue.at(x-1).at(y-1) != nil)]);
			});
			neighbors.add([(x-1),(y),(organismQueue.at(x-1).at(y) != nil)]);
			if(y < (this.width - 1), {
				neighbors.add([(x-1),(y+1),(organismQueue.at(x-1).at(y+1) != nil)]);
			});
		});
		if(x < (this.length - 1), {
			if(y > 0, {
				neighbors.add([(x+1),(y-1),(organismQueue.at(x+1).at(y-1) != nil)]);
			});
			neighbors.add([(x+1),(y),(organismQueue.at(x+1).at(y) != nil)]);
			if(y < (this.width - 1), {
				neighbors.add([(x+1),(y+1),(organismQueue.at(x+1).at(y+1) != nil)]);
			});

		});

		if(y > 0, {
			neighbors.add([(x),(y-1),(organismQueue.at(x).at(y-1) != nil)]);
		});
		if(y < (this.width - 1), {
			neighbors.add([(x),(y+1),(organismQueue.at(x).at(y+1) != nil)]);
		});
		^neighbors.asArray.choose;
	}

	createNeighbor { |organism|
		var temp = this.getNeighbors(organism.posX, organism.posY);
		if(temp[2], {
			this.killOrganism(this.organismQueue.at(temp[0]).at(temp[1]));
		});
		this.organismQueue.at(temp[0]).put(temp[1],
			GrainOrganism.new(organism.nodes, temp[0], temp[1])
		);
	}
}
