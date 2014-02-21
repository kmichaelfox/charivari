GrainOrganism {
	classvar <>numOrganisms;

	var <>nodes, <>sequence, <>automation, <>posX, <>posY;

	*initClass{
		Class.initClassTree(List);
		Class.initClassTree(Server);
		Class.initClassTree(Routine);
		GrainOrganism.numOrganisms = 0;
	}

	*new{ | chain = nil, posX = 0, posY = 0 |
		^super.new.init(chain, posX, posY);
	}

	init{ |chain, posX, posY|
		// increment GrainOrganism instance counter
		GrainOrganism.numOrganisms_(GrainOrganism.numOrganisms + 1);
		GrainOrganism.numOrganisms.postln;

		// initialize nodes as empty list for grains
		this.nodes = List.new;

		// if argument chain is nil, create a new grain for node, else copy chain to nodes
		if(chain == nil, {
			this.addNode()
		},{
			this.nodes = chain;
		});

		// initialize position variables
		this.posX = posX;
		this.posY = posY;

		// initialize sequencer as Psequence stream
		this.sequence = Pseq(this.nodes, inf).asStream;

		// initialize automation as inf loop Routine which sends synth messages to server
		// automation cycles over nodes via sequence to determine the message
		this.automation = Routine({
			loop ({
				var currentNode = this.sequence.next;
				switch(currentNode.at(0))
				{\sinegrain} {
					Server.local.sendMsg("s_new",
						\gabor0, -1, 0, 0,
						\freq, (currentNode.at(1)),
						\sustain, (currentNode.at(2)),
						\x, this.posX,
						\y, this.posY
					);
					(currentNode.at(2)+currentNode.at(3)).wait;
				}
				{\glisson} {
					Server.local.sendMsg("s_new",
						\gaborGliss, -1, 0, 0,
						\freq_start, (currentNode.at(1)),
						\freq_end, (currentNode.at(2)),
						\sustain, (currentNode.at(3)),
						\x, this.posX,
						\y, this.posY
					);
					(currentNode.at(3)+currentNode.at(4)).wait;
				};
			})
		}).play;
	}

	addNode {
		var nodeToAppend = List.new;

		var grainType = 2.rand;

		switch(grainType,
			0, {
				//"sine grain".postln;

				// add identifier string
				nodeToAppend.add(\sinegrain);

				// random frequency 20-4000 Hz
				nodeToAppend.add(this.generateFreq);

				// random grain duration 1-50 ms
				nodeToAppend.add(this.generateGrainDur);

				// grain offset duration 1-400 ms
				nodeToAppend.add(this.generateOffsetDur);
			},
			1, {
				//"glisson grain".postln;

				// add identifier string
				nodeToAppend.add(\glisson);

				// random starting frequency 20-4000 Hz
				nodeToAppend.add(this.generateFreq);

				// random ending frequency 20-4000 Hz
				nodeToAppend.add(this.generateFreq);

				// random grain duration 1-50 ms
				nodeToAppend.add(this.generateGrainDur);

				// grain offset duration 1-100 ms
				nodeToAppend.add(this.generateOffsetDur);
			}
		);
		this.nodes.add(nodeToAppend);
	}

	// stop the sequence, free the sequencer, and decrement GrainOrganism counter
	die {
		this.automation.stop;
		this.automation.free;
		GrainOrganism.numOrganisms_(GrainOrganism.numOrganisms - 1);
		GrainOrganism.numOrganisms.postln;
	}

	mutate {
		var nodeToChange = this.nodes.at((this.nodes.size).rand);
		var paramToChange = ((nodeToChange.size - 1).rand + 1);
		var router = 2.rand;

		if(this.nodes.size >= 30, {
			// "node chain is full".postln;
			router = 2;
		});

		switch(router)
		{0} {
			this.addNode;
		}
		{1} {
			switch(nodeToChange.at(0))
			{\sinegrain}{
				switch(paramToChange,
					1,{nodeToChange.put(paramToChange, this.generateFreq);},
					2,{nodeToChange.put(paramToChange, this.generateGrainDur);},
					3,{nodeToChange.put(paramToChange, this.generateOffsetDur);}
				);
			}
			{\glisson}{
				switch(paramToChange,
					1, {nodeToChange.put(paramToChange, this.generateFreq);},
					2, {nodeToChange.put(paramToChange, this.generateFreq);},
					3, {nodeToChange.put(paramToChange, this.generateGrainDur);},
					4, {nodeToChange.put(paramToChange, this.generateOffsetDur);}
				);
			};
		}
		{2} {
			if(this.nodes.size > 2, {
				//"dropping nodes".postln:
				this.nodes.removeAt((this.nodes.size).rand);
			});
		};
	}

	// generate a randomized frequency value
	generateFreq {
		^(exprand(20, 4000));
	}

	// generate a randomized grain duration
	generateGrainDur {
		^((50.rand+1)/1000.0)
	}

	// generate a randomized grain offset duration
	generateOffsetDur {
		^((100.rand+1)/1000.0)
	}
}