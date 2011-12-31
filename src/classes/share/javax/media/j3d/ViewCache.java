/*
 * $RCSfile$
 *
 * Copyright 1997-2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 *
 * $Revision$
 * $Date$
 * $State$
 */

package javax.media.j3d;

import javax.vecmath.Point3d;

/**
 * The ViewCache class is used to cache all data, both API data and derived
 * data, that is independent of the Canvas3D and Screen3D.
 */
class ViewCache extends Object {
    // The view associated with this view cache
    View view;

    //
    // API/INPUT DATA
    //

    // *********************
    // * From ViewPlatform *
    // *********************
    int		viewAttachPolicy;

    // *********************
    // * From PhysicalBody *
    // *********************

    /**
     * The user's left eye's position in head coordinates.
     */
    Point3d	leftEyePosInHead = new Point3d();

    /**
     * The user's right eye's position in head coordinates.
     */
    Point3d	rightEyePosInHead = new Point3d();

    /**
     * The user's left ear's position in head coordinates.
     */
    Point3d	leftEarPosInHead = new Point3d();

    /**
     * The user's right ear's position in head coordinates.
     */
    Point3d	rightEarPosInHead = new Point3d();

    /**
     * The user's nominal eye height as measured
     * from the ground plane.
     */
    double	nominalEyeHeightFromGround;

    /**
     * The amount to offset the system's
     * viewpoint from the user's current eye-point.  This offset
     * distance allows an "Over the shoulder" view of the scene
     * as seen by the user.
     */
    double	nominalEyeOffsetFromNominalScreen;

    // Head to head-tracker coordinate system transform.
    // If head tracking is enabled, this transform is a calibration
    // constant.  If head tracking is not enabled, this transform is
    // not used.
    // This is only used in SCREEN_VIEW mode.
    Transform3D headToHeadTracker = new Transform3D();

    // ****************************
    // * From PhysicalEnvironment *
    // ****************************

    // Coexistence coordinate system to tracker-base coordinate
    // system transform.  If head tracking is enabled, this transform
    // is a calibration constant.  If head tracking is not enabled,
    // this transform is not used.
    // This is used in both SCREEN_VIEW and HMD_VIEW modes.
    Transform3D coexistenceToTrackerBase = new Transform3D();

    // Transform generated by the head tracker to transform
    // from tracker base to head tracker coordinates (and the inverse).
    Transform3D headTrackerToTrackerBase = new Transform3D();
    Transform3D trackerBaseToHeadTracker = new Transform3D();

    //
    // Indicates whether the underlying hardware implementation
    // supports tracking.
    //
    boolean trackingAvailable;

    // Sensor index for head tracker
    int headIndex;

    //
    // This variable specifies the policy Java 3D will use in placing
    // the user's eye position relative to the user's head position
    // (NOMINAL_SCREEN, NOMINAL_HEAD, or NOMINAL_FEET).
    // It is used in the calibration process.
    //
    int coexistenceCenterInPworldPolicy;


    // *************
    // * From View *
    // *************

    // View model compatibility mode flag
    boolean compatibilityModeEnable;

    // coexistenceCenteringEnable flag
    boolean coexistenceCenteringEnable;

    Point3d leftManualEyeInCoexistence = new Point3d();
    Point3d rightManualEyeInCoexistence = new Point3d();

    // Indicates which major mode of view computation to use:
    // HMD mode or screen/fish-tank-VR mode.
    int		viewPolicy;

    // The current projection policy (parallel versus perspective)
    int projectionPolicy;

    // The current screen scale policy and scale value
    int screenScalePolicy;
    double screenScale;

    // The current window resize, movement and eyepoint policies
    int windowResizePolicy;
    int windowMovementPolicy;
    int windowEyepointPolicy;

    // The current monoscopic view policy
    int monoscopicViewPolicy;

    // The view model's field of view.
    double	fieldOfView;

    // The distance away from the clip origin
    // in the direction of gaze for the front and back clip planes.
    double	frontClipDistance;
    double	backClipDistance;

    // Front and back clip policies
    int		frontClipPolicy;
    int		backClipPolicy;

    // ViewPlatform of this view
    ViewPlatformRetained vpRetained;

    /**
     * Defines the visibility policy.
     */
    int		visibilityPolicy;

    // Flag to enable tracking, if so allowed by the trackingAvailable flag.
    boolean		trackingEnable;

    // This setting enables the continuous updating by Java 3D of the
    // userHeadToVworld transform.
    boolean userHeadToVworldEnable;

    // The current compatibility mode view transform
    Transform3D compatVpcToEc = new Transform3D();

    // The current compatibility mode projection transforms
    Transform3D compatLeftProjection = new Transform3D();
    Transform3D compatRightProjection = new Transform3D();

    // Mask that indicates ViewCache's view dependence info. has changed,
    // and CanvasViewCache may need to recompute the final view matries.
    int vcDirtyMask = 0;

    //
    // DERIVED DATA
    //

    // Flag indicating that head tracking will be used
    private boolean doHeadTracking;

    //
    // Matrix to transform from user-head to
    // virtual-world coordinates. This matrix is a read-only
    // value that Java 3D generates continuously, but only if enabled
    // by userHeadToVworldEnableFlag.
    //
    Transform3D	userHeadToVworld = new Transform3D();


    /**
     * Take snapshot of all per-view API parameters and input values.
     */
    synchronized void snapshot() {

	// View parameters
	vcDirtyMask = view.vDirtyMask;
	view.vDirtyMask = 0;
	compatibilityModeEnable = view.compatibilityModeEnable;
	coexistenceCenteringEnable = view.coexistenceCenteringEnable;
	leftManualEyeInCoexistence.set(view.leftManualEyeInCoexistence);
	rightManualEyeInCoexistence.set(view.rightManualEyeInCoexistence);
	viewPolicy = view.viewPolicy;
	projectionPolicy = view.projectionPolicy;
	screenScalePolicy = view.screenScalePolicy;
	windowResizePolicy = view.windowResizePolicy;
	windowMovementPolicy = view.windowMovementPolicy;
	windowEyepointPolicy = view.windowEyepointPolicy;
	monoscopicViewPolicy = view.monoscopicViewPolicy;

	fieldOfView = view.fieldOfView;
	screenScale = view.screenScale;

	frontClipDistance = view.frontClipDistance;
	backClipDistance = view.backClipDistance;
	frontClipPolicy = view.frontClipPolicy;
	backClipPolicy = view.backClipPolicy;

	visibilityPolicy = view.visibilityPolicy;

	trackingEnable = view.trackingEnable;
	userHeadToVworldEnable = view.userHeadToVworldEnable;

	view.compatVpcToEc.getWithLock(compatVpcToEc);
	view.compatLeftProjection.getWithLock(compatLeftProjection);
	view.compatRightProjection.getWithLock(compatRightProjection);

	// ViewPlatform parameters
	ViewPlatform vpp = view.getViewPlatform();

	if (vpp == null) {
	    // This happens when user attach a null viewplatform
	    // and MC still call updateViewCache() in run before
	    // the viewDeactivate request get.
	    return;
	}

	vpRetained = (ViewPlatformRetained) vpp.retained;

	synchronized(vpRetained) {
	    vcDirtyMask |= vpRetained.vprDirtyMask;
	    vpRetained.vprDirtyMask = 0;
	    viewAttachPolicy = vpRetained.viewAttachPolicy;
	    // System.err.println("ViewCache snapshot vcDirtyMask " + vcDirtyMask );
	}

	// PhysicalEnvironment parameters
	PhysicalEnvironment env = view.getPhysicalEnvironment();

	synchronized(env) {
	    vcDirtyMask |= env.peDirtyMask;
	    env.peDirtyMask = 0;

	    env.coexistenceToTrackerBase.getWithLock(coexistenceToTrackerBase);
	    trackingAvailable = env.trackingAvailable;
	    coexistenceCenterInPworldPolicy = env.coexistenceCenterInPworldPolicy;

	    // NOTE: this is really derived data, but we need it here in order
	    // to avoid reading head tracked data when no tracker is available
	    // and enabled.
	    doHeadTracking = trackingEnable && trackingAvailable;

	    if (doHeadTracking) {
		headIndex = env.getHeadIndex();
		env.getSensor(headIndex).getRead(headTrackerToTrackerBase);
		vcDirtyMask |= View.TRACKING_ENABLE_DIRTY;
	    }
	    else {
		headTrackerToTrackerBase.setIdentity();
	    }
	}

	// PhysicalBody parameters
	PhysicalBody body = view.getPhysicalBody();

	synchronized(body) {
	    vcDirtyMask |= body.pbDirtyMask;
	    body.pbDirtyMask = 0;

	    leftEyePosInHead.set(body.leftEyePosition);
	    rightEyePosInHead.set(body.rightEyePosition);
	    leftEarPosInHead.set(body.leftEarPosition);
	    rightEarPosInHead.set(body.rightEarPosition);

	    nominalEyeHeightFromGround = body.nominalEyeHeightFromGround;
	    nominalEyeOffsetFromNominalScreen =
		body.nominalEyeOffsetFromNominalScreen;
	}

	body.headToHeadTracker.getWithLock(headToHeadTracker);
    }


    /**
     * Compute derived data using the snapshot of the per-view data.
     */
    synchronized void computeDerivedData() {
	if (doHeadTracking) {
	    trackerBaseToHeadTracker.invert(headTrackerToTrackerBase);
	    //System.err.println("trackerBaseToHeadTracker: ");
	    //System.err.println(trackerBaseToHeadTracker);
	}
	else {
	    trackerBaseToHeadTracker.setIdentity();
	}

	// XXXX: implement head to vworld tracking if userHeadToVworldEnable is set
    	userHeadToVworld.setIdentity();
    }


    // Get methods for returning derived data values.
    // Eventually, these get functions will cause some of the parameters
    // to be lazily evaluated.
    //
    // NOTE that in the case of Transform3D, and Tuple objects, a reference
    // to the actual derived data is returned.  In these cases, the caller
    // must ensure that the returned data is not modified.

    boolean getDoHeadTracking() {
	return doHeadTracking;
    }

    /**
     * Constructs and initializes a ViewCache object.
     */
    ViewCache(View view) {
	this.view = view;

	if (false)
	    System.err.println("Constructed a ViewCache");
    }

}
