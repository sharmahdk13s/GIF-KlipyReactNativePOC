module.exports = {
  dependency: {
    platforms: {
      ios: {},
      android: {
        sourceDir: "android",
        // Disable codegen since we're using a manual spec
        codegen: false,
      },
    },
  },
};
