# 10 - Functional Analysis of Scenes

Understanding possible interactions with objects (i.e. their *function*)

Not the object *type*, but more: e.g. to know how to open a drawer, we also need to find its handle. Which parts are the interactable parts?

Examples:
- pushcart: *wheel* - spinning -> allows movement
- fan: *rotor* - spinning -> allows to blow air
- what pose would a person be in when using a hammer/a banana/binoculars?

### Subtask: Predicting interactions
Related: predicting interaction poses (which pose does a person have when watching TV/writing on a whiteboard etc.). Can have a time component as well.

(Example: video with a horse -> can we forecast what the woman does next?)


##### Challenges
- data acquisition: how do we get enough and diverse enough data for such a task?
- how to represent objects/scene/person? (Maybe a graph?)


##### PiGraphs [Savva et al. '16]
Tracked human actors with motion capture. Learnt graph representation:
- each body part (skeleton part) and each object part becomes a node
- goal: learn co-occurence of these (e.g. "hip joint <-> sitting <-> chairs")

based on input texts ("watch-TV+restfeet-stool"), generate interaction snapshot.
![[pigraphs.png]]

Limitation: very limited because of the small training set.

### Human motion extraction
##### iMapper [Monspart et al. '18]
Get RGB video as input; Generates synthesized scene with human motion on a skeleton.
![[iMapper-input-output.png]]

Training: with short interaction videos (*scenelests*) from PiGraph data.
First fit the skeleton statically, then perform a *scenelet fitting* using the database, then refine.

### Generating Human-Object interaction snapshots
##### Table object interaction generation [Wang et al. ‘19]
Train on interactions of a guy sitting at a table; using off-the-shelf image object recognition. Then synthesize new interactions to get animations of objects moving around.

##### Predicting pose snapshots [Li et al. '19]
Predicting static pose snapshots that showcase how the objects can be used.

extract
- semantic knowledge (skeletons of persons in videos)
- geometry knowledge

=> predict skeletons poses in 3d environment

##### Generating 3D People in Scenes [Zhang et al. '20]
Scene given. Generate bodies with VAE, use SMPL-X 3D body representation.
Then fit the human body to the scene to avoid floating/collisions.

(no architecture details given)

##### Populating 3D Scenes [Hassan et al. '21]
More or less trying to achieve the same as the previous one (also uses SMPL-X).


### Predicting human motion
![[predict-human-motion.png]]

##### Human motion prediction with scene context [Cao et al. '20]
Input: image + 2D pose history, Sample possible 2D destinations and predict the 3D path to the destinations.

Synthetic data generated from GTA game (persons moving in scene).

1. predict goal where person is moving (*GoalNet*)
	- heatmap over possible locations
2. predict single trajectory - i.e. where the hip moves, in 2D, without moving the full skeleton (*PathNet*)
	-  set of 2d heatmaps over time
3. predict movement of all skeleton joints (*PoseNet*)
	- transformer: start with 2d trajectory -> project to 3d as noisy input; then apply transformer to refine.
	- attention on pose level: attends over previous poses 

Excursion: transformers and attention (see slides


##### Forecasting Characteristic Poses [Diller et al. '21]
Goal: predict not just random poses, but characteristic poses of certain actions. Specifically, start with an input pose and simulate the next poses over time when a certain action is performed.

Multi-modal prediction distribution: e.g. left hand maybe be passing or close to body (if the right hand is passing). Predicting the average of both is bad => auto-regressive prediction: predict joints in sequential order.

Achieved using attention (over previous predictions). Then decodes into 3d heatmap (aka probability distribution).

![[forecasting-characteristic-poses.png]]

### Simulation Environments
Virtual 3D environments for training/testing AI agents
- no tedious real-world data collection and no material cost
- simulate rare/dangerous scenarios
- easier to reproduce stuff

##### Habitat
3D agent simulator (efficient rendering, on existing 3D datasets)

Has benchmark tasks:
- PointNav: agent at random position in unseen environment must navigate to target position
- ObjectNav: agent at random position in unseen environment must find instance of an object category

##### AI2Thor
Basic physics, partly dynamic objects

##### Gibson
Works with scanned data.

##### SAPIEN
PartNet has some objects with annotated parts where motion parameters (how far can it move) are specified. SAPIEN focuses on simulating interaction with such objects.