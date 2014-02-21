// Stereo reduction SynthDefs

(
SynthDef(\gabor0, {| out, freq = 440, sustain = 0.02, amp = 0.04, pan, x = 0, y = 0 |
	var env = EnvGen.ar(Env.sine(sustain, amp * (1 - (y/45))), doneAction: 2);
	var sound = SinOsc.ar(freq) * env;
	OffsetOut.ar(out, Pan2.ar(sound, /*pan*/ (x-40)/40))
}, \ir.dup(5)).load();
)

(
SynthDef(\gaborGliss, {| out, freq_start = 440, freq_end = 880, sustain = 0.02, amp = 0.04, pan, x = 0, y = 0 |
	var env = EnvGen.ar(Env.sine(sustain, amp * (1 - (y/45))), doneAction: 2);
	var sound = SinOsc.ar(XLine.kr(freq_start, freq_end, sustain, doneAction: 2)) * env;
	OffsetOut.ar(out, Pan2.ar(sound, /*pan*/ (x-40)/40))
}, \ir.dup(5)).load();
)

// 8-Channel SynthDefs

(
SynthDef(\gabor0, {| out, freq = 440, sustain = 0.02, amp = 0.02, pan, x = 0, y = 0 |
	var env = EnvGen.ar(Env.sine(sustain, amp), doneAction: 2);
	var sound = SinOsc.ar(freq) * env;
	OffsetOut.ar(out, [
		// channel 1
		MulAdd(sound, 1 - (((((20-x).abs).squared) + (((0-y).abs).squared)).sqrt / 40).clip(0,1)),
		// channel 2
		MulAdd(sound, 1 - (((((60-x).abs).squared) + (((0-y).abs).squared)).sqrt / 40).clip(0,1)),
		// channel 3
		MulAdd(sound, 1 - (((((80-x).abs).squared) + (((11-y).abs).squared)).sqrt / 40).clip(0,1)),
		// channel 4
		MulAdd(sound, 1 - (((((80-x).abs).squared) + (((34-y).abs).squared)).sqrt / 40).clip(0,1)),
		// channel 5
		MulAdd(sound, 1 - (((((60-x).abs).squared) + (((45-y).abs).squared)).sqrt / 40).clip(0,1)),
		// channel 6
		MulAdd(sound, 1 - (((((20-x).abs).squared) + (((45-y).abs).squared)).sqrt / 40).clip(0,1)),
		// channel 7
		MulAdd(sound, 1 - (((((0-x).abs).squared) + (((33-y).abs).squared)).sqrt / 40).clip(0,1)),
		// channel 8
		MulAdd(sound, 1 - (((((0-x).abs).squared) + (((11-y).abs).squared)).sqrt / 40).clip(0,1))
		]
	)
}, \ir.dup(5)).load();
)

(
SynthDef(\gaborGliss, {| out, freq_start = 440, freq_end = 880, sustain = 0.02, amp = 0.02, pan, x = 0, y = 0 |
	var env = EnvGen.ar(Env.sine(sustain, amp), doneAction: 2);
	var sound = SinOsc.ar(XLine.kr(freq_start, freq_end, sustain, doneAction: 2)) * env;
	OffsetOut.ar(out, [
		// channel 1
		MulAdd(sound, 1 - (((((20-x).abs).squared) + (((0-y).abs).squared)).sqrt / 40).clip(0,1)),
		// channel 2
		MulAdd(sound, 1 - (((((60-x).abs).squared) + (((0-y).abs).squared)).sqrt / 40).clip(0,1)),
		// channel 3
		MulAdd(sound, 1 - (((((80-x).abs).squared) + (((11-y).abs).squared)).sqrt / 40).clip(0,1)),
		// channel 4
		MulAdd(sound, 1 - (((((80-x).abs).squared) + (((34-y).abs).squared)).sqrt / 40).clip(0,1)),
		// channel 5
		MulAdd(sound, 1 - (((((60-x).abs).squared) + (((45-y).abs).squared)).sqrt / 40).clip(0,1)),
		// channel 6
		MulAdd(sound, 1 - (((((20-x).abs).squared) + (((45-y).abs).squared)).sqrt / 40).clip(0,1)),
		// channel 7
		MulAdd(sound, 1 - (((((0-x).abs).squared) + (((33-y).abs).squared)).sqrt / 40).clip(0,1)),
		// channel 8
		MulAdd(sound, 1 - (((((0-x).abs).squared) + (((11-y).abs).squared)).sqrt / 40).clip(0,1))
		]
	)
}, \ir.dup(5)).load();
)