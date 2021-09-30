## Finite Element Modelling

What we need to know:

- Kinetic equations
- Material model and stress-strain relation
- Equilibrium equation

What it does:

- Models complex geometry by using small geometric elements of which we know the physical behaviour

Benefits:

- Handle complex geometry
- Handle complex loading
- Wide range of engineering problems
- Investigate different materials, geometries, loadings
- Digital prototyping (crash test e.g.)

Steps for FEM:

- Pre-processing
  - Import geometry (STL format required, facetted, not like smooth CAD) and choosing the right mesh (should be fine enough)
  - Model materials (can be hard for biological tissues not well known)
  - Bound modelling (values at bounds of differentiable equation, loading conditions)
  - Contact modelling (model contact between implant and bone)
- Solve mathematical equations
  - Linear/non-linear, iterative methods for non-linear
- Post-processing (evaluation)
  - Biological values can vary from person to person
  - Remodelling of tissues for humans
  - Critical failure values hard to determine

Optimization is the process of finding the best solution for our FEM problem, definitions:

- Design parameters (can change them, given from 3d structural analysis)
- Constraint functions (no of screws, max movement allowed)
- Objective function (what we are looking for in the optimization)

Boundary conditions are calculated:

- By doing tests in a motion analysis environment

## Navigation in Trauma Surgery

Why:

- Using imaging modalities can help navigate inside the body during operation and help a potential robot. Used for preoperative or intraoperative surgery

Aims:

- Reduce operation time
- Reduce operation trauma
- Better long-term results
- Reduce complications during surgery
- Better accuracy

Problems:

- Sterility
- Human-machine interaction
- Eye-hand coordination

Modalities:

- Mono-modal
  - CT (easy to segment but radiating)
  - C-Arm (the best one for intraoperative navigation)
  - MRI (problems with segmentation and matching)
  - SPECT/CT
- Multi-modal

Performable operations:

- Pedicle screw (placement of screws in the pedicle of the vertebra)
- KTEP (Knee Total Endo Prothesis)
- Specimen biopsy
- Osteotomy of the roof of the femur

Problems with MRI navigation:

- Segmentation
- Matching
- Distortion

Pros:

- Visualization (multiplanar images, improved clarity using colourful segmentation, flexible directions of view)
- 3D Modelling (biomechanical evaluation and 3D planning)
- Planning (choose the correct implant, virtual positioning of implant and feasibility check)
- Data Fusion (multimodal techniques, integrate functional findings)
- Tools for education (virtual simulations)
- Documentation (preoperative and postoperative)
- Reduced radiation (for staff and patients)
- Accuracy (to check for reproducibility)
- Minimal invasive (small cuts)

Cons:

- Increased radiation (preoperative CT scan might be needed)
- Increased OR time (more blood loss)
- Bigger incisions (for robots, they need linear entrance points)
- No haptic feedback (for robots)
- Learning curve (new technology that needs to be learnt by doctors, also the focus during operation could drift from patient to navigation system)
- Psychology (because doctor works on a virtual image, separation from doctor to physical patient)
- Bigger rooms required (because robots need space)

## Anatomy

Symptoms:

- Range of Motion diminished
- Functionality compromised
- Pain

What we need to know to do diagnosis:

- Anatomy
- Pathology
- Imaging

Types of tissues:

- Bone
- Cartilage
- Synovial fluid
- Joint capsule
- Tendons/ligaments

Proximal vs Distal

Planes:

- Frontal plane (seen from the front doesn&#39;t change)
- Axial plane (seen from the top doesn&#39;t change)
- Sagittal plane (seen from the side doesn&#39;t change)

Imaging techniques:

- MRI
- PET/SPECT
- CT
- Radiography
- Ultrasound

Range of Motion is the movement a joint can make away or towards the body from the 0° position

Shoulder:

- 5 joints, not very stable
- Kept together by plenty of muscles

Elbow:

- Can do supination/pronation
- Governed by bicep

Hip:

- Extremely stable
- All types of rotation available

Knee:

- Menisci to keep joint in place
- Roto-translation
- Crociato ligament

Ankle:

- Not stable
- Many ligaments to keep together

## Biomechanics Lab

Tissue engineering:

- Bioreactor, create substitute tissue for kidneys
- Cultivation inside a cell
- Station for cleaning bones and cadaveric material

Material testing, stress-strain:

- Wohler diagram: for each force do a series of loadings and plot at which number of iterations the material broke, after 2 million the material is considered to have infinite life. Measure of **fatigue**
- Creep: happens when the load is kept for a long period of time, this deformation is time-dependent
- Anisotropy: material behaves differently when put to test under different directions

Mandibular reconstruction:

- Usually done with industry produced pieces that are not patient specific (ring). When a tumour destroys the mandibular bone, I could cut some bone from the femur/fibula and place it in the mandibula thanks to patient specific rings.

Implants:

- Hip, intramedullary, knee
- Both for horses and humans
- Robot to do testing
- Stress-strain machine for horse implants, huge

## Materialise, 3D printing

Generic operation workflow:

- Diagnosis based on CT data
- 3D planning with physician help to determine cutting planes for desired outcome
- Drill inside bone following the guides
- Cut through the bone following the guides

Treatment possibilities for Craniomaxillofacial surgery:

- Cranioplasties
- Zygoma implants
- Mandibular reconstruction
- Orbital floors

For Orthopaedics:

- Hip dysplasia
- Hip revision (for implants that have been placed years ago, come la revision dell &#39;auto)
- Shoulder implants
- Patient specific plates
- Bone osteotomies
- Support structure for bones
- Treatment for tumour cases

Choose technique and material carefully depending on application:

- Material extrusion
  - Pros: cheap
  - Considerations: can see the layers, requires mechanical post-processing, uses support for building
  - Could be used for device prototyping and orthopaedics/CMF anatomical models
- VAT photo-polymerization
  - Pros: available in desktop format
  - Considerations: needs chemical washing and mechanical post processing, discoloration after time
  - Could be used for CMF, orthopaedics and vascular models
- Powdered bed fusion
  - Pros: sterilizable, no supports created, durable
  - Considerations: creates dust in the making
  - CMF and orthopaedics models, surgical implants and guides
- Binder jetting
  - Pros: multi-coloured
  - Considerations: brittle material (ceramic behaviour), dust in the process
  - Various anatomical models
- Material jetting
  - Pros: multicoloured, multi-material including polymers
  - Consideration: high costs, heavy post-processing
  - Cardiac, vascular and soft tissue modelling

Anatomical models useful for:

- Redefine surgical operation
- Pre-operation planning
- Training and education
- Patient education
- Reduced operation time
- Reduced re-admissions

## Neuro-prosthetics

Types of neuro-prostheses:

- Motor-prostheses
  - Walking
  - Hand
- Sensory prostheses
  - Cochlear implants
  - Retina implants
- Brain-Computer Interface
- Brain-Brain Interface
- Transcranial magnetic stimulation
- Functional electrical stimulation
  - For muscle stimulation
- Neuromodulation
  - Parkinson disease
  - Obesity fighting implant
  - Pacemaker

## Surgical Sonification

Surgical sonification is useful to give additional information and data to the surgeon performing operation.

Why:

- Channel that has not been used since now
- Lot of surgical events still pass unnoticed during operations
- Requires effortless interpretation
- Provides direction information
- Complex info in short period of time
- Immediate feedback
- Draws attention

Different sonification techniques:

- Auditory icons (ex. Paper crumbling when deleting a document)
- Earcons (ex. Melody when receiving a message)
- Audification (data is the sound itself, ex. ECG pressure signal converted to sound)
- Parameter Mapping Sonification (ex. Data is not directly converted to sound, but it regulates parameters that go on to create the sound)
- Model Based Sonification (data becomes an instrument, then user interaction with the model generates sound)
- Wave Space Sonification (data-driven sampling of a high dimensional sound space)

Different sound synthesis techniques:

- Additive synthesis (add different basis sine functions together to get one as complex as I like)
- Subtractive synthesis (start from a complex sound function and remove unnecessary parts)
- Frequency Modulation method (create a signal by frequency modulation of a starting signal)
- Physical Modelling (model thanks to equations and mathematical models a physical object that can then generate a sound)
- Wave shaping synthesis (concept of function concatenation)
- Granular synthesis (samples that can be layered on top of each other)
- Concatenative synthesis (chops of music or sound played consecutively)

Applications:

- Navigation
- Surgery soundtrack
- Auditory augmented reality (selective removal of unnecessary noise from the landscape, useful in the OR)

## Surgical Robotics and Ophthalmic Imaging

Why surgical robotics:

- Less invasiveness
- More accuracy/precision
- Increase number of performable operations
- Enhanced documentation

Fields of robots in healthcare:

- Rehabilitation treatments assistance
- Rehabilitation robotics
- Surgical robotics

Robot vs Human:

- Ergonomic
- Less invasive
- Can learn faster
- Doesn&#39;t require good hand-eye coordination
- Reduction of tremors
- No fatigue

Classification of robots:

- Programmable robots (didn&#39;t work quite well, should do everything on its own)
- Master-Slave systems (like DaVinci, robot controlled by
- Surgical assistance robots

Ophthalmic imaging techniques:

- Optical imaging
- OCT (optical coherence tomography, A-Scan, B-Scan, cube image)
  - Time domain (too slow)
  - Frequency domain
- MRI
- Ultrasound

Eye problems:

- Anterior segment
  - Cataract (foggy lens)
- Posterior segment
  - Macular Hole (removal of the damaged retina layer)
  - AMD retina (collection of proteins under the retina. Solution requires periodically drug injections to prevent the spread of the disease but without curing. New robotic machine can perform this injection operation in a better way allowing for a singular injection and limiting the amount of drug injected.
