# marble

MARBLE project contains two parts:

Capture: the capture program runs on a Raspberry Pi. It extracts 3D key points from camera images. The code is in CaptureVideo/

Client: the client program runs on an Android Phone. It reads the 3D key points data from BLE broadcast packages. After that it renders the avatar on the Phone screen. It performs localization and pose estimation in realtime, so that the rendering above camera preview generates an augmented reality experience. The code is in marble_client.

For questions or suggestions. Please file an issue.
